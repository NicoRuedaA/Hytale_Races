import os
import json

def update_textures():
    root_path = os.getcwd()
    print(f"--- Iniciando escaneo RECURSIVO en: {root_path} ---")
    
    archivos_modificados = 0

    # os.walk recorre subcarpetas automáticamente
    for root, dirs, files in os.walk(root_path):
        for filename in files:
            if filename.endswith(".json"):
                file_path = os.path.join(root, filename)
                
                with open(file_path, 'r', encoding='utf-8') as f:
                    try:
                        data = json.load(f)
                    except Exception:
                        continue

                target = None
                parent = None

                if "Appearance" in data and "TextureConfig" in data["Appearance"]:
                    target = data["Appearance"]["TextureConfig"]
                    parent = data["Appearance"]
                elif "TextureConfig" in data:
                    target = data["TextureConfig"]
                    parent = data
                
                if target and "Texture" in target:
                    old_path = target["Texture"]
                    new_path = old_path.replace("Characters/Wardrobe/", "")

                    parent["TextureConfig"] = {
                        "Type": "Gradient",
                        "GradientSet": "Hair",
                        "GrayscaleTexture": new_path
                    }

                    with open(file_path, 'w', encoding='utf-8') as f:
                        json.dump(data, f, indent=2)
                    
                    # Imprime la ruta relativa para saber dónde está el archivo
                    rel_path = os.path.relpath(file_path, root_path)
                    print(f"[OK] Textura: {rel_path}")
                    archivos_modificados += 1

    print(f"\nTotal de archivos modificados: {archivos_modificados}")

if __name__ == "__main__":
    update_textures()