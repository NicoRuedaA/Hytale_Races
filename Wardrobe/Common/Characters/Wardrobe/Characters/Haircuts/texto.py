import os
import re

def procesar_jsons():
    # Obtener la ruta del directorio actual
    directorio_actual = os.getcwd()
    
    # Expresión regular para buscar .png" al final de una línea
    # El símbolo $ indica el final de la cadena
    patron = r'\.png"'
    sustitucion = '_Greyscale.png"'

    print(f"Buscando archivos .json en: {directorio_actual}\n")

    for nombre_archivo in os.listdir(directorio_actual):
        if nombre_archivo.endswith(".json"):
            ruta_completa = os.path.join(directorio_actual, nombre_archivo)
            
            # Leer el contenido del archivo
            with open(ruta_completa, 'r', encoding='utf-8') as f:
                contenido = f.read()

            # Realizar la sustitución
            nuevo_contenido = re.sub(patron, sustitucion, contenido)

            # Solo sobreescribir si hubo cambios para ahorrar recursos
            if nuevo_contenido != contenido:
                with open(ruta_completa, 'w', encoding='utf-8') as f:
                    f.write(nuevo_contenido)
                print(f"✅ Procesado: {nombre_archivo}")
            else:
                print(f"➖ Sin cambios: {nombre_archivo}")

if __name__ == "__main__":
    procesar_jsons()