package com.laptopshop.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptopshop.dto.AiChatRequest;
import com.laptopshop.dto.AiChatResponse;
import com.laptopshop.dto.AiProductSuggestion;
import com.laptopshop.dto.ProductFilterRequest;
import com.laptopshop.entity.Brand;
import com.laptopshop.entity.Category;
import com.laptopshop.entity.Product;
import com.laptopshop.repository.BrandRepository;
import com.laptopshop.repository.CategoryRepository;
import com.laptopshop.repository.ProductRepository;
import com.laptopshop.repository.specification.ProductSpecification;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 10;

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.ai.gemini.key:}")
    private String geminiKey;

    @Value("${app.ai.gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    @Transactional(readOnly = true)
    public AiChatResponse chat(AiChatRequest request) {
        if (request == null || !StringUtils.hasText(request.getMessage())) {
            throw new ResponseStatusException(BAD_REQUEST, "Message is required");
        }
        if (!StringUtils.hasText(geminiKey)) {
            throw new ResponseStatusException(BAD_REQUEST, "GEMINI_KEY is not configured");
        }

        int limit = resolveLimit(request.getLimit());
        
        // 1. Phân tích Nhu cầu
        ExtractionResult extraction = extractIntent(request.getMessage());
        ProductFilterRequest intent = extraction.getIntent();
        log.info("=== EXTRACTED INTENT ===\n{}", intent);

        // 2. Query Database Động
        List<Product> products = productRepository.findAll(
                ProductSpecification.filter(intent, false),
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"))
        ).getContent();

        List<AiProductSuggestion> suggestions = toSuggestions(products);
        String prompt = buildPrompt(request.getMessage().trim(), suggestions);
        
        log.info("=== GENERATED AI PROMPT ===\n{}", prompt);

        GeminiResponse response = webClientBuilder.build()
                .post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={key}",
                        geminiModel, geminiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "contents", List.of(
                                Map.of(
                                        "role", "user",
                                        "parts", List.of(Map.of("text", prompt))
                                )
                        )
                ))
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .block(Duration.ofSeconds(20));

        String reply = response != null ? response.firstText() : "";
        
        // Ghi toàn bộ quá trình ra file log
        writeLogToFile(request.getMessage(), extraction.getPrompt(), intent, suggestions, prompt, reply);

        return AiChatResponse.builder()
                .reply(reply != null ? reply : "")
                .suggestions(suggestions)
                .build();
    }

    @Getter
    @RequiredArgsConstructor
    private static class ExtractionResult {
        private final ProductFilterRequest intent;
        private final String prompt;
    }

    private ExtractionResult extractIntent(String userMessage) {
        String availableBrands = brandRepository.findAll().stream()
            .map(b -> b.getId() + " - " + b.getName())
            .collect(Collectors.joining(", "));
        String availableCategories = categoryRepository.findAll().stream()
            .map(c -> c.getId() + " - " + c.getName())
            .collect(Collectors.joining(", "));

        String prompt = "Bạn là một AI trích xuất dữ liệu (Data Extractor). Hãy đọc tin nhắn của người dùng và trích xuất các điều kiện tìm kiếm laptop dưới định dạng JSON chính xác.\n" +
                "LƯU Ý: Tuyệt đối không được bịa dữ liệu. Chỉ sử dụng các giá trị ID được cung cấp dưới đây.\n\n" +
                "Các trường của JSON:\n" +
                "- keyword: CHỈ dùng để lọc Tên Dòng Máy hoặc Model (ví dụ: XPS, Thinkpad, Pavilion, Victus, Inspiron, MacBook). TUYỆT ĐỐI KHÔNG đưa các tính từ (tốt, đẹp, rẻ, mượt) hoặc nhu cầu (chơi game, văn phòng) vào trường này. Trả về null nếu không có.\n" +
                "- brandId: BẮT BUỘC CHỌN 1 ID TRONG DANH SÁCH SAU NẾU CÓ, KHÔNG CÓ TRẢ VỀ null: [" + availableBrands + "].\n" +
                "- categoryId: BẮT BUỘC CHỌN 1 ID TRONG DANH SÁCH SAU NẾU CÓ, KHÔNG CÓ TRẢ VỀ null: [" + availableCategories + "].\n" +
                "- minPrice: Giá tối thiểu (kiểu số nguyên). Ví dụ: 10 triệu -> 10000000. Trả về null nếu không có.\n" +
                "- maxPrice: Giá tối đa (kiểu số nguyên). Ví dụ: 20 triệu -> 20000000. Trả về null nếu không có.\n" +
                "- cpu: Thông tin CPU (ví dụ: i5, i7, Ryzen 5, Ultra 7). Trả về null nếu không có.\n" +
                "- ram: Thông tin RAM (ví dụ: 8GB, 16GB, 32GB). Trả về null nếu không có.\n" +
                "- storage: Thông tin Ổ cứng (ví dụ: 512GB, 1TB). Trả về null nếu không có.\n" +
                "- gpu: Thông tin Card đồ họa (ví dụ: RTX 4050, RTX 3060). Trả về null nếu không có.\n" +
                "- screen: Thông tin Màn hình (ví dụ: 14 inch, 15.6 inch, OLED). Trả về null nếu không có.\n\n" +
                "Tin nhắn: \"" + userMessage + "\"\n\n" +
                "CHỈ TRẢ VỀ JSON, KHÔNG GIẢI THÍCH GÌ THÊM. KHÔNG DÙNG ```json.";

        try {
            GeminiResponse response = webClientBuilder.build()
                    .post()
                    .uri("https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={key}",
                            geminiModel, geminiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                            "contents", List.of(
                                    Map.of(
                                            "role", "user",
                                            "parts", List.of(Map.of("text", prompt))
                                    )
                            )
                    ))
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .block(Duration.ofSeconds(10));

            String text = response != null ? response.firstText() : "";
            if (StringUtils.hasText(text)) {
                text = text.trim();
                if (text.startsWith("```json")) text = text.substring(7);
                if (text.startsWith("```")) text = text.substring(3);
                if (text.endsWith("```")) text = text.substring(0, text.length() - 3);
                ProductFilterRequest parsed = objectMapper.readValue(text.trim(), ProductFilterRequest.class);
                return new ExtractionResult(parsed, prompt);
            }
        } catch (Exception e) {
            log.error("Failed to extract intent from AI", e);
        }
        return new ExtractionResult(new ProductFilterRequest(), prompt);
    }

    private void writeLogToFile(String userMessage, String extractionPrompt, ProductFilterRequest intent, List<AiProductSuggestion> suggestions, String prompt, String reply) {
        try {
            java.io.File file = new java.io.File("ai-chat-log.txt");
            try (java.io.FileWriter fw = new java.io.FileWriter(file, true);
                 java.io.BufferedWriter bw = new java.io.BufferedWriter(fw)) {
                
                bw.write("=================================================================\n");
                bw.write("=== THỜI GIAN: " + java.time.LocalDateTime.now() + " ===\n");
                bw.write("=================================================================\n\n");
                
                bw.write("[1] CÂU HỎI TỪ NGƯỜI DÙNG:\n");
                bw.write(userMessage + "\n\n");

                bw.write("[1.2] PROMPT BÓC TÁCH (DATA EXTRACTOR):\n");
                bw.write(extractionPrompt + "\n\n");

                bw.write("[1.5] KẾT QUẢ AI BÓC TÁCH REQUEST (ProductFilterRequest):\n");
                bw.write("JSON: " + intent + "\n\n");
                
                bw.write("[2] PROMPT GỬI AI:\n");
                bw.write(prompt + "\n\n");
                
                bw.write("[3] CÂU TRẢ LỜI CỦA AI:\n");
                bw.write(reply + "\n\n");
                
                bw.write("[4] SUGGESTIONS:\n");
                bw.write(suggestions.toString() + "\n\n");
            }
        } catch (Exception e) {
            log.error("Could not write AI log to file", e);
        }
    }

    private int resolveLimit(Integer requested) {
        if (requested == null || requested <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(requested, MAX_LIMIT);
    }

    private List<AiProductSuggestion> toSuggestions(List<Product> products) {
        List<AiProductSuggestion> results = new ArrayList<>();
        for (Product product : products) {
            String brandName = product.getBrand() != null ? product.getBrand().getName() : null;
            String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
            results.add(AiProductSuggestion.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .brandName(brandName)
                    .categoryName(categoryName)
                    .build());
        }
        return results;
    }

    private String buildPrompt(String message, List<AiProductSuggestion> suggestions) {
        StringBuilder builder = new StringBuilder();
        builder.append("You are a laptop shop assistant. Answer in Vietnamese.\n");
        builder.append("Suggest suitable products from the list when relevant.\n\n");
        builder.append("Available products:\n");
        for (AiProductSuggestion item : suggestions) {
            builder.append("- id: ").append(item.getId())
                    .append(", name: ").append(item.getName());
            if (StringUtils.hasText(item.getBrandName())) {
                builder.append(", brand: ").append(item.getBrandName());
            }
            if (StringUtils.hasText(item.getCategoryName())) {
                builder.append(", category: ").append(item.getCategoryName());
            }
            builder.append("\n");
        }
        
        if (suggestions.isEmpty()) {
            builder.append("(No products found)\n");
        }

        builder.append("\nUser question: ").append(message).append("\n");
        builder.append("Provide a concise helpful answer and include product ids if you recommend any.\n");
        builder.append("IMPORTANT: If the 'Available products' list is empty, you MUST politely reply 'Xin lỗi, hiện tại cửa hàng không có sản phẩm nào hoàn toàn khớp với yêu cầu của bạn.' and DO NOT hallucinate or recommend any other products.");
        return builder.toString();
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GeminiResponse {
        private List<Candidate> candidates;

        String firstText() {
            if (candidates == null || candidates.isEmpty()) {
                return "";
            }
            Candidate candidate = candidates.get(0);
            if (candidate == null || candidate.content == null || candidate.content.parts == null) {
                return "";
            }
            for (Part part : candidate.content.parts) {
                if (part != null && StringUtils.hasText(part.text)) {
                    return part.text;
                }
            }
            return "";
        }
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Candidate {
        private Content content;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Content {
        private List<Part> parts;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Part {
        private String text;
    }
}
