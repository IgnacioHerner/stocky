# Decisiones técnicas

- UI: Jetpack Compose
- Arquitectura: MVVM (sin Clean Architecture estricta en MVP)
- Persistencia: Room + Flow
- Base de datos local como fuente de verdad
- Moneda MVP: ARS
- Dinero: Double (MVP, sujeto a mejora)
- minSdk: 26

# Decisiones técnicas

## Persistencia de datos

- Se utiliza Room como base de datos local.
- La base de datos es la fuente de verdad de la app.
- El acceso a datos se realiza exclusivamente a través de DAOs.

## Diseño de DAOs

- Los DAOs se definen como interfaces anotadas con @Dao.
- Las funciones de lectura devuelven Flow para permitir reactividad.
- Las funciones de escritura son suspend (insert, update, delete).
- Los cálculos agregados (sumas, totales) se realizan en SQL dentro del DAO.
- El DAO no contiene lógica de negocio ni conoce la UI.

## Arquitectura

- La UI no accede directamente al DAO.
- El flujo de acceso a datos es:
  UI → ViewModel → Repository → DAO → Base de datos.
- 
## Database (Room)

- Se utiliza Room como base de datos local.
- La Database es el único punto de acceso a la base de datos.
- Las Entities se registran explícitamente en la Database.
- Los DAOs se acceden únicamente a través de la Database.
- En el MVP se usa versión 1 sin migraciones.
- La Database no contiene lógica de negocio ni consultas directas.


## Repository

- Se utiliza un Repository como capa entre ViewModel y DAO.
- El resto de la app no accede directamente a DAOs.
- El Repository expone lecturas reactivas (Flow) y acciones suspend.
- En MVP el Repository delega directamente al DAO, dejando preparada la estructura para escalar.

## Dependencias e inyección (MVP)

- Se usa un AppContainer (inyección manual) en lugar de Hilt para el MVP.
- La base de datos se crea una sola vez al iniciar la aplicación.
- Los ViewModels reciben sus dependencias (Repository) por constructor.
- Se usa ViewModelFactory para instanciar ViewModels con dependencias.
- Los estados de UI se exponen mediante StateFlow.

## UI (MVP)

- La UI se construye con Jetpack Compose.
- Las pantallas observan el estado del ViewModel usando StateFlow.
- Se utiliza collectAsStateWithLifecycle para observar Flows de forma segura.
- En el MVP se usa un “producto dummy” para validar inserción y reactividad antes del formulario real.

## Alta de producto (MVP)

- El alta de productos se implementa inicialmente con un AlertDialog para acelerar el MVP.
- Los campos del formulario se manejan como String y se convierten al guardar.
- Validación mínima: nombre obligatorio, números parseables y valores no negativos.
- La inserción se realiza a través del ViewModel/Repository (la UI no habla con Room).


## Edición de producto (MVP)

- Se reutiliza el mismo formulario para alta y edición (ProductFormDialog).
- El modo se determina por initialProduct (null = add, no-null = edit).
- Para editar se usa copy() preservando el id, y se llama a update() en el ViewModel.

## Stock bajo y filtro

- La lógica de stock bajo vive en el ViewModel (no en la UI).
- Se agrega lowStockProducts como dato derivado.
- El estado del filtro (showOnlyLowStock) es estado de UI y vive en ProductsScreen.
- ProductsScreen decide qué lista pasar a ProductsContent.
- ProductsContent solo dibuja lo que recibe.
- 
## Relaciones entre tablas

- Se utiliza @Relation para modelar 1 a muchos.
- Se usa @Transaction para evitar lecturas inconsistentes.
- insertSale devuelve el ID generado para poder asociar items.
- SaleWithItems no es tabla, solo modelo de lectura.

