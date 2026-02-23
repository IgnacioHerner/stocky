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
## Módulo Ventas – Modelado

- Se modela Sale y SaleItem para soportar múltiples productos por venta.
- La fecha se guarda como timestamp (Long) para ordenar y filtrar por rango.
- SaleItem guarda unitPrice para mantener histórico del precio al momento de la venta.
- Se usa CASCADE en Sale → SaleItem para evitar items huérfanos.

## Relaciones en Room (Ventas)

- Se usa un modelo de lectura (SaleWithItems) para representar Sale + items.
- Se utiliza @Relation para modelar 1 a muchos (Sale.id → SaleItem.saleId).
- Se usa @Transaction para lecturas consistentes cuando hay relaciones.
- insertSale devuelve el id generado para asociar los ítems de la venta.
- 
## SalesRepository – Inserción de venta completa

- La inserción compuesta (venta + ítems) se implementa en Repository, no en DAO.
- Se usa database.withTransaction para atomicidad (todo o nada).
- Se usa un modelo de entrada (NewSaleItem) para desacoplar la creación de ventas de las Entities.

## Registrar venta (atomicidad y stock)

- El registro de venta se implementa en SalesRepository (operación compuesta).
- Se usa database.withTransaction para garantizar rollback si falla cualquier paso.
- El total se calcula en Repository para no depender de la UI.
- El stock se valida antes de aplicar el descuento y se lanza una excepción si es insuficiente.

## Nueva venta (MVP)

- Se implementa primero una venta con 1 producto para validar el flujo completo.
- La UI no calcula el total ni descuenta stock: delega al Repository.
- Se captura InsufficientStockException para informar al usuario.
- Navigation se pospone; se conecta temporalmente desde MainActivity para testear.

## Carrito de venta (múltiples items)

- Se modela el carrito con un modelo UI (CartItemUi) separado de Entities.
- El carrito vive en el ViewModel para mantener consistencia y escalar.
- El total se calcula como dato derivado (no se persiste).
- registerSale delega al Repository para transacción + descuento de stock.

## Navigation Compose

- Se centraliza la navegación en un único NavHost en MainActivity.
- Las pantallas no conocen NavController: solo emiten eventos (callbacks).
- Rutas definidas en un objeto Routes para evitar strings hardcodeados.

## Historial de ventas

- El filtrado por fecha se realiza en DAO (query SQL) por performance.
- Se usa un modelo UI (SaleSummaryUi) para desacoplar Entities de la UI.
- La pantalla muestra un resumen por venta (fecha, total, cantidades).

## Navegación

- La navegación se centraliza en StockyNavGraph (único lugar con NavController).
- Las pantallas reciben callbacks (onNewSaleClick, onSalesHistoryClick, onBack) para no acoplarse al NavController.
- Se usa popBackStack() para back explícito en TopAppBar.

## Filtro por fecha

- El filtrado se realiza en DAO (BETWEEN) por performance.
- Se normalizan fechas a inicio/fin del día para evitar excluir ventas por hora.
- El ViewModel usa un flujo de rango + flatMapLatest para cancelar observaciones anteriores automáticamente.

## Eliminar venta con consistencia

- Se restaura stock al eliminar una venta para mantener inventario correcto.
- La operación se hace dentro de withTransaction (atomicidad).
- SaleDetailViewModel es parametrizado por saleId, por eso requiere Factory con parámetro.
