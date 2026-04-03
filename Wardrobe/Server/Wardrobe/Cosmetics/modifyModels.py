import os
import json

def update_models():
    root_path = os.getcwd()
    print(f"--- Iniciando limpieza RECURSIVA de Models en: {root_path} ---")
    
    archivos_modificados = 0

    for root, dirs, files in os.walk(root_path):
        for filename in files:
            if filename.endswith(".json"):
                file_path = os.path.join(root, filename)
                
                with open(file_path, 'r', encoding='utf-8') as f:
                    try:
                        data = json.load(f)
                    except Exception:
                        continue

                if "Appearance" in data and "Model" in data["Appearance"]:
                    old_model_path = data["Appearance"]["Model"]
                    
                    if "Characters/Wardrobe/" in old_model_path:
                        new_model_path = old_model_path.replace("Characters/Wardrobe/", "")
                        data["Appearance"]["Model"] = new_model_path

                        with open(file_path, 'w', encoding='utf-8') as f:
                            json.dump(data, f, indent=2)
                        
                        rel_path = os.path.relpath(file_path, root_path)
                        print(f"[OK] Modelo: {rel_path}")
                        archivos_modificados += 1

    print(f"\nTotal de modelos actualizados: {archivos_modificados}")

if __name__ == "__main__":
    update_models()