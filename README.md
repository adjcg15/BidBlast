## Control de versiones

Todos los nombrados descritos en esta sección, a pesar de estar explicados en español, deberán hacerse
en inglés en la práctica.

### Nombrado de commits

Cada commit deberá estar nombrado siguiendo la siguiente especificación:

```tag(archivo o módulo afectado): descripción extendida de lo que realiza el commit```

El `tag` es una etiqueta estandar definida por los colaboradores:

* `feat`: indica que el commit está incluyendo un `feature` o funcionalidad nueva.
* `config`: indica que el commit está cambiando algún archivo de configuración, instaló alguna biblioteca 
para el proyecto o cambió la estructura de organización de algún módulo o carpeta.
* `fix`: indica que el commit está realizando un cambio sobre alguna funcionalidad existente, ya sea para resolver
un bug, cambiar la lógica o mejorar el rendimiento.
* `docs`: indica que el commit está realizando un cambio en documentos del repositorio fuera del código, como 
el archivo md README que se encuentra leyendo.
* `revert`: reversión a un commit anterior.
* `test`: indica que el commit agrega, corrige o elimina una prueba unitaria del proyecto

Tome en cuenta que la _descripción extendida de lo que realiza el commit_ debe estar escrita desde el punto de vista
de este. Ejemplo:

`feat(app.ts): implements call to AuthRouter and exposes its services`

Note cómo se usan las palabras "implements" y "exposes" para indicar que el commit está realizando o implementando
esto. Un mal nombrado de un commit sería violando esta regla. Ejemplo:

`feat(app.ts): implementation of AuthRouter exposition of services`

### Nombrado de pull requests (PR)

Cada PR deberá estar nombrado siguiendo la siguiente especificación:

```TAG: nombre representativo de lo que se incluye```

El `tag` cumple una función parecida al `tag` de un commit, pues indica el tipo de PR que se realiza, sin embargo, en este
caso hay cinco posibles opciones: `FEAT`, `FIX` `CONFIG`, `DOCS` y `TEST`. Se espera que habiendo explicado sus significados
para un commit, el lector pueda intuir sus significados en un PR.

En este caso, el _nombre representativo de lo que se incuye_ en el PR, **no** debe estar escrito como en el commit, es decir, 
"desde su punto de vista". En este caso, sí debe colocarse de la forma:

```FEAT: implementation of login endpoint```

En lugar de:

```FEAT: implements login endpoint```

Tome en cuenta que un PR es un "hito", trate de describir ese hito. 

### Nombrado de ramas

Se recomienda que, para llevar una mejor organización de las ramas, estas se nombren de la siguiente forma:

```referencia-caso-uso/tag/descripcion```

Por ejemplo, si el caso de uso es "Evaluar artículo para subastar" y se desea implementar un `feature` que englobe la 
construcción de un endpoint asociado, un nombre recomendable sería:

```auction-evaluation/feat/endpoint-descriptive-name```

Mientras que un nombre no recomendado sería:

```endpoint_name```
