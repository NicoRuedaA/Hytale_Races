import os
import json

# Prefijo requerido para las rutas internas del JSON
PREFIJO_RUTA = 'Characters/Wardrobe/Cosmetics'

def generar_archivos():
    conteo = 0
    # Listamos las subcarpetas dentro de Cosmetics (Head, Face, Arms, etc.)
    # Obtenemos solo los nombres de las carpetas actuales antes de crear las nuevas
    subcarpetas_originales = [d for d in os.listdir('.') if os.path.isdir(d) and not d.startswith('.')]

    for subcarpeta in subcarpetas_originales:
        ruta_sub = os.path.join('.', subcarpeta)
        
        # El slot ahora es exactamente el nombre de la carpeta (ej: "Face")
        slot_name = subcarpeta
        
        # Carpeta de salida para los JSON (se creará en la raíz de Cosmetics)
        # Para evitar conflictos, la llamaremos con un sufijo temporal o en una ruta clara
        # Aquí la guardaremos en una carpeta llamada "JSON_OUTPUT/NombreDelSlot"
        ruta_destino_base = "GENERATED_JSONS"
        ruta_destino_especifica = os.path.join(ruta_destino_base, slot_name)

        files = os.listdir(ruta_sub)
        
        # Buscamos archivos .blockymodel
        for file in files:
            if file.endswith('.blockymodel'):
                nombre_base = os.path.splitext(file)[0]
                
                # Ruta de la carpeta de texturas
                nombre_carpeta_texturas = f"{nombre_base}_Textures"
                ruta_texturas_full = os.path.join(ruta_sub, nombre_carpeta_texturas)
                
                if os.path.exists(ruta_texturas_full) and os.path.isdir(ruta_texturas_full):
                    # Buscamos el primer archivo .png
                    archivos_textura = [f for f in os.listdir(ruta_texturas_full) if f.endswith('.png')]
                    
                    if archivos_textura:
                        nombre_png_real = archivos_textura[0]
                        
                        # Estructura del JSON
                        json_data = {
                          "Properties": {
                            "Translation": {
                              "Name": f"Races_Vanilla_{nombre_base}"
                            }
                          },
                          "CosmeticSlot": slot_name,
                          "Appearance": {
                            "Model": f"{PREFIJO_RUTA}/{subcarpeta}/{nombre_base}.blockymodel",
                            "TextureConfig": {
                              "Texture": f"{PREFIJO_RUTA}/{subcarpeta}/{nombre_carpeta_texturas}/{nombre_png_real}"
                            }
                          }
                        }

                        # Crear carpetas de salida si no existen
                        if not os.path.exists(ruta_destino_especifica):
                            os.makedirs(ruta_destino_especifica)

                        # Guardar el JSON
                        nombre_archivo_json = f"{nombre_base}.json"
                        ruta_final = os.path.join(ruta_destino_especifica, nombre_archivo_json)
                        
                        with open(ruta_final, 'w', encoding='utf-8') as f:
                            json.dump(json_data, f, indent=2)
                        
                        print(f"✅ [{subcarpeta}] -> {ruta_destino_especifica}/{nombre_archivo_json}")
                        conteo += 1

    if conteo == 0:
        print("❌ No se encontraron modelos para procesar.")
    else:
        print(f"\n--- Finalizado: {conteo} archivos creados en la carpeta 'GENERATED_JSONS' ---")

if __name__ == "__main__":
    generar_archivos()