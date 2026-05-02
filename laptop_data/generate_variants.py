import json
import random

with open('final_gallery_data.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

for product in data:
    variants = product.get('variants', [])
    if not variants:
        continue
    
    base_variant = variants[0]
    base_price = base_variant.get('price', 15000000)
    base_specs = base_variant.get('specs', {})
    
    new_variants = [base_variant]
    
    # Generate an upgraded variant (e.g. more RAM or Storage)
    upgraded_variant = base_variant.copy()
    upgraded_specs = base_specs.copy()
    
    # Simple logic to upgrade RAM or Storage
    if '16GB' in upgraded_specs.get('ram', ''):
        upgraded_specs['ram'] = '32GB'
        upgraded_price = base_price + 3000000
    elif '8GB' in upgraded_specs.get('ram', ''):
        upgraded_specs['ram'] = '16GB'
        upgraded_price = base_price + 1500000
    elif '512GB' in upgraded_specs.get('storage', ''):
        upgraded_specs['storage'] = '1TB'
        upgraded_price = base_price + 2000000
    else:
        upgraded_specs['storage'] = '1TB'
        upgraded_price = base_price + 2000000
        
    upgraded_variant['specs'] = upgraded_specs
    upgraded_variant['price'] = upgraded_price
    upgraded_variant['sku'] = base_variant['sku'] + '-upgraded'
    new_variants.append(upgraded_variant)
    
    product['variants'] = new_variants

with open('final_gallery_data_with_variants.json', 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

print("Created final_gallery_data_with_variants.json")
