import os
import json

def generar_jsons():
    # Directorio actual donde se ejecuta el script
    directorio_actual = os.getcwd()
    
    # Extensiones a buscar
    extension_modelo = ".blockymodel"
    
    # Contador para saber cuántos archivos procesamos
    procesados = 0

    print("--- Iniciando generación de archivos JSON ---")

    for archivo in os.listdir(directorio_actual):
        if archivo.endswith(extension_modelo):
            # Extraemos el nombre sin la extensión
            nombre_base = os.path.splitext(archivo)[0]
            
            # Estructura del diccionario según tu plantilla
            data = {
                "Properties": {
                    "Translation": {
                        "Name": f"Races_Vanilla_{nombre_base}"
                    }
                },
                "CosmeticSlot": "Cape",
                "Appearance": {
                    "Model": f"Characters/Wardrobe/Cosmetics/Capes/{nombre_base}.blockymodel",
                    "TextureConfig": {
                        "Texture": f"Characters/Wardrobe/Cosmetics/Capes/Cape_Leaves_Textures/{nombre_base}.png"
                    }
                }
            }

            # Nombre del nuevo archivo JSON
            nombre_json = f"{nombre_base}.json"

            # Escribir el archivo JSON
            with open(nombre_json, 'w', encoding='utf-8') as f:
                json.dump(data, f, indent=2, ensure_ascii=False)
            
            print(f"Generado: {nombre_json}")
            procesados += 1

    print("---")
    print(f"Proceso finalizado. Se han creado {procesados} archivos JSON.")

if __name__ == "__main__":
    generar_jsons()