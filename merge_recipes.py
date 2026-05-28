import json
import os

resources_dir = os.path.join("src", "main", "resources")

files_to_merge = [
    "seed_recipes_mediterranean_1.json",
    "seed_recipes_mediterranean_2.json",
    "seed_recipes_thai.json"
]

all_recipes = []

for filename in files_to_merge:
    path = os.path.join(resources_dir, filename)
    if os.path.exists(path):
        with open(path, "r", encoding="utf-8") as f:
            data = json.load(f)
            recipes = data.get("recipes", [])
            print(f"Loaded {len(recipes)} recipes from {filename}")
            all_recipes.extend(recipes)
    else:
        print(f"Warning: {filename} does not exist!")

output_data = {"recipes": all_recipes}
output_path = os.path.join(resources_dir, "seed_recipes.json")

with open(output_path, "w", encoding="utf-8") as f:
    json.dump(output_data, f, indent=2, ensure_ascii=False)

print(f"Merged total of {len(all_recipes)} recipes and saved to {output_path}")
