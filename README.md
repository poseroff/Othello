=== COMO JUGAR REVERSI ===

1) Clonar este repositorio
2) Abrir una terminal
3) Moverse a la carpeta del proyecto llamada Othello
3) Crear el ejecutable con el comando "mvn clean install"
4) Moverse a la carpeta target con el siguiente comando: "cd ./target"
5) Ejecutar tpe.jar pasándole los parámetros del juego utilizando el siguiente formato, donde los corchetes deberán ser reemplazados por el valor deseado:
        "java -jar tpe.jar -size [n] -ai [m] -mode [time|depth] -param [k] -prune [on|off] -load [file]"
