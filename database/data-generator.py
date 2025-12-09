#!/usr/bin/env python3
"""
Data Generator for REST API Benchmark
Generates CSV files for PostgreSQL COPY command
- 2000 categories (CAT0001..CAT2000)
- 100000 items (~50 per category)
"""

import csv
import random
from datetime import datetime, timedelta
from pathlib import Path

# Configuration
NUM_CATEGORIES = 2000
NUM_ITEMS = 100000
OUTPUT_DIR = Path(__file__).parent / "generated"

# Product name components
ADJECTIVES = ["Premium", "Standard", "Deluxe", "Classic", "Modern", "Vintage", 
              "Professional", "Advanced", "Basic", "Elite", "Superior", "Compact"]
PRODUCTS = ["Widget", "Gadget", "Tool", "Device", "Component", "Module", 
            "Unit", "System", "Kit", "Set", "Package", "Bundle"]
MATERIALS = ["Steel", "Aluminum", "Plastic", "Carbon", "Titanium", "Copper", 
             "Bronze", "Ceramic", "Composite", "Alloy"]
COLORS = ["Red", "Blue", "Green", "Black", "White", "Silver", "Gold", 
          "Gray", "Orange", "Purple"]

CATEGORY_TYPES = ["Electronics", "Tools", "Furniture", "Clothing", "Sports", 
                  "Books", "Toys", "Food", "Garden", "Automotive", "Health", 
                  "Beauty", "Jewelry", "Music", "Office", "Pet", "Baby", 
                  "Art", "Industrial", "Medical"]


def random_date(start_days_ago=365):
    """Generate random timestamp within last N days"""
    days_ago = random.randint(0, start_days_ago)
    dt = datetime.now() - timedelta(days=days_ago, 
                                    hours=random.randint(0, 23),
                                    minutes=random.randint(0, 59))
    return dt.strftime('%Y-%m-%d %H:%M:%S')


def generate_category_name(idx):
    """Generate realistic category name"""
    cat_type = CATEGORY_TYPES[idx % len(CATEGORY_TYPES)]
    if idx % 3 == 0:
        return f"{cat_type} - {random.choice(ADJECTIVES)}"
    elif idx % 3 == 1:
        return f"{random.choice(ADJECTIVES)} {cat_type}"
    else:
        return cat_type


def generate_item_name():
    """Generate realistic item name"""
    templates = [
        f"{random.choice(ADJECTIVES)} {random.choice(PRODUCTS)}",
        f"{random.choice(COLORS)} {random.choice(PRODUCTS)}",
        f"{random.choice(MATERIALS)} {random.choice(PRODUCTS)}",
        f"{random.choice(ADJECTIVES)} {random.choice(MATERIALS)} {random.choice(PRODUCTS)}",
    ]
    return random.choice(templates)


def generate_categories():
    """Generate categories CSV"""
    print(f"Generating {NUM_CATEGORIES} categories...")
    
    OUTPUT_DIR.mkdir(exist_ok=True)
    output_file = OUTPUT_DIR / "categories.csv"
    
    with open(output_file, 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['id', 'code', 'name', 'updated_at'])
        
        for i in range(1, NUM_CATEGORIES + 1):
            code = f"CAT{i:04d}"
            name = generate_category_name(i)
            updated_at = random_date()
            
            writer.writerow([i, code, name, updated_at])
    
    print(f"✓ Categories written to {output_file}")
    return NUM_CATEGORIES


def generate_items(num_categories):
    """Generate items CSV with distribution across categories"""
    print(f"Generating {NUM_ITEMS} items (~{NUM_ITEMS//num_categories} per category)...")
    
    OUTPUT_DIR.mkdir(exist_ok=True)
    output_file = OUTPUT_DIR / "items.csv"
    
    # Distribute items across categories (roughly equal, with some variation)
    items_per_category = [NUM_ITEMS // num_categories] * num_categories
    remaining = NUM_ITEMS - sum(items_per_category)
    
    # Distribute remaining items randomly
    for _ in range(remaining):
        items_per_category[random.randint(0, num_categories - 1)] += 1
    
    with open(output_file, 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['id', 'sku', 'name', 'price', 'stock', 'category_id', 'updated_at'])
        
        item_id = 1
        for category_id in range(1, num_categories + 1):
            for _ in range(items_per_category[category_id - 1]):
                sku = f"SKU{item_id:06d}"
                name = generate_item_name()
                price = round(random.uniform(9.99, 999.99), 2)
                stock = random.randint(0, 1000)
                updated_at = random_date()
                
                writer.writerow([item_id, sku, name, price, stock, category_id, updated_at])
                item_id += 1
    
    print(f"✓ Items written to {output_file}")


def generate_jmeter_data(num_categories, num_items):
    """Generate CSV files for JMeter tests"""
    print("Generating JMeter test data...")
    
    jmeter_dir = OUTPUT_DIR.parent.parent / "jmeter" / "data"
    jmeter_dir.mkdir(parents=True, exist_ok=True)
    
    # Category IDs for tests
    with open(jmeter_dir / "category-ids.csv", 'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(['categoryId'])
        for i in range(1, num_categories + 1):
            writer.writerow([i])
    
    # Item IDs for tests
    with open(jmeter_dir / "item-ids.csv", 'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(['itemId'])
        for i in range(1, num_items + 1):
            writer.writerow([i])
    
    # Sample payloads for POST/PUT requests
    # Light payload (~0.5-1 KB)
    with open(jmeter_dir / "item-payload-light.csv", 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['sku', 'name', 'price', 'stock', 'categoryId'])
        for i in range(1000):
            sku = f"JMETER{i:06d}"
            name = generate_item_name()
            price = round(random.uniform(9.99, 999.99), 2)
            stock = random.randint(0, 1000)
            category_id = random.randint(1, num_categories)
            writer.writerow([sku, name, price, stock, category_id])
    
    # Heavy payload (~5 KB) - with description field
    with open(jmeter_dir / "item-payload-heavy.csv", 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['sku', 'name', 'price', 'stock', 'categoryId', 'description'])
        for i in range(1000):
            sku = f"JMHEAVY{i:06d}"
            name = generate_item_name()
            price = round(random.uniform(9.99, 999.99), 2)
            stock = random.randint(0, 1000)
            category_id = random.randint(1, num_categories)
            # Generate ~4-5 KB description
            description = " ".join([f"Lorem ipsum dolor sit amet, consectetur adipiscing elit. {random.choice(ADJECTIVES)}" for _ in range(100)])
            writer.writerow([sku, name, price, stock, category_id, description])
    
    # Category payloads
    with open(jmeter_dir / "category-payload.csv", 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['code', 'name'])
        for i in range(1000):
            code = f"JMCAT{i:04d}"
            name = generate_category_name(i)
            writer.writerow([code, name])
    
    print(f"✓ JMeter data written to {jmeter_dir}")


def generate_statistics():
    """Print statistics about generated data"""
    print("\n" + "="*60)
    print("DATA GENERATION SUMMARY")
    print("="*60)
    print(f"Categories: {NUM_CATEGORIES:,}")
    print(f"Items: {NUM_ITEMS:,}")
    print(f"Average items per category: {NUM_ITEMS // NUM_CATEGORIES}")
    print(f"Output directory: {OUTPUT_DIR.absolute()}")
    print("\nFiles generated:")
    print(f"  - {OUTPUT_DIR}/categories.csv")
    print(f"  - {OUTPUT_DIR}/items.csv")
    print(f"  - jmeter/data/category-ids.csv")
    print(f"  - jmeter/data/item-ids.csv")
    print(f"  - jmeter/data/item-payload-light.csv")
    print(f"  - jmeter/data/item-payload-heavy.csv")
    print(f"  - jmeter/data/category-payload.csv")
    print("\nTo load data into PostgreSQL:")
    print("  psql -U postgres -h localhost -d benchmark -c \"\\COPY category FROM 'generated/categories.csv' CSV HEADER\"")
    print("  psql -U postgres -h localhost -d benchmark -c \"\\COPY item FROM 'generated/items.csv' CSV HEADER\"")
    print("="*60)


def main():
    """Main execution"""
    print("REST API Benchmark - Data Generator")
    print("="*60)
    
    # Generate data
    num_cats = generate_categories()
    generate_items(num_cats)
    generate_jmeter_data(num_cats, NUM_ITEMS)
    
    # Statistics
    generate_statistics()


if __name__ == "__main__":
    main()
