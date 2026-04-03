import os
import json

def actualizar_jsons():
    # Obtener la ruta del directorio actual
    directorio_actual = os.getcwd()
    
    # Listar todos los archivos en el directorio
    archivos = [f for f in os.listdir(directorio_actual) if f.endswith('.json')]
    
    if not archivos:
        print("No se encontraron archivos .json en esta carpeta.")
        return

    for nombre_archivo in archivos:
        # Extraer el nombre base (ej: 'nombre' de 'nombre.json')
        nombre_base = os.path.splitext(nombre_archivo)[0]
        
        # Definir la estructura de datos
        datos = {
            "Properties": {
                "Translation": {
                    "Name": f"Races_Vanilla_{nombre_base}"
                }
            },
            "CosmeticSlot": "FacialHair",
            "Appearance": {
                "Model": f"Characters/Wardrobe/Characters/Body_Attachments/Beards/{nombre_base}.blockymodel",
                "TextureConfig": {
                    "Texture": f"Characters/Wardrobe/Characters/Body_Attachments/Beards/{nombre_base}_Greyscale.png"
                }
            }
        }
        
        # Escribir el contenido en el archivo
        try:
            with open(nombre_archivo, 'w', encoding='utf-8') as f:
                json.dump(datos, f, indent=2, ensure_ascii=False)
            print(f"Actualizado con éxito: {nombre_archivo}")
        except Exception as e:
            print(f"Error al procesar {nombre_archivo}: {e}")

if __name__ == "__main__":
    actualizar_jsons()