# Bitácora de bloques

## Unidad 1 - Persistencia

### Bloque 1.1 - Entity Product
- Se creó ProductEntity para representar la tabla products.
- Aprendí la diferencia entre Entity y lógica de negocio.
- Decisión: nombres de campos en inglés.

### Bloque 1.2 – ProductDao (DAO)

En este bloque se trabajó el concepto de DAO (Data Access Object).

Aprendí que un DAO es la capa encargada exclusivamente de acceder a la base
de datos. Su responsabilidad es definir qué operaciones se pueden hacer
sobre una tabla: leer, insertar, actualizar, eliminar y calcular métricas.

El DAO:
- Es una interface anotada con @Dao.
- No tiene lógica de negocio.
- No conoce la UI ni el ViewModel.
- Solo trabaja con SQL y Entities.

Se entendió la diferencia entre dos tipos de funciones:
- Lecturas reactivas: devuelven Flow y se actualizan automáticamente cuando
  cambia la base de datos.
- Escrituras puntuales: usan suspend y modifican la base (insert, update,
  delete).

También se vio que:
- Los cálculos como totales de inventario deben hacerse en el DAO usando SQL.
- Las funciones de agregación deben evitar devolver null (por ejemplo usando
  valores por defecto).
- El DAO es un contrato: Room genera la implementación automáticamente.

Resultado del bloque:
Entiendo qué es un DAO, para qué sirve y cómo encaja dentro de la arquitectura,
aunque todavía no lo implemente completamente solo.

### Bloque 1.3 – Database (Room)

En este bloque se implementó la Database de Room.

La Database es la clase que conecta las Entities con los DAOs y crea la base
de datos SQLite real. Es el punto de entrada único a la base de datos local
y garantiza que exista una sola instancia durante la ejecución de la app.

Se definió una Database abstracta que:
- Registra las Entities del proyecto.
- Expone los DAOs mediante funciones abstractas.
- Define la versión del esquema de datos.

Se entendió que la Database:
- No ejecuta queries.
- No contiene lógica de negocio.
- Solo organiza y centraliza el acceso a datos.

Resultado del bloque:
Room quedó completo a nivel estructura (Entity + DAO + Database).

### Bloque 1.4 – Repository (ProductRepository)

En este bloque se implementó el Repository como capa intermedia entre la app y Room.

El Repository existe para evitar que el resto de la app dependa directamente de DAOs
y detalles de persistencia. Su función es ofrecer una API de datos más limpia y
preparar el proyecto para escalar (por ejemplo, agregando API, caché o lógica de
coordinación).

Se implementó ProductRepository:
- Expone lecturas reactivas (Flow) para productos y métricas.
- Expone acciones puntuales suspend para insertar, actualizar y eliminar.
- Delegación directa al DAO (MVP).

Resultado del bloque:
La app ya tiene una “puerta de entrada” a datos que desacopla Room del resto del sistema.

### Bloque 1.5 – AppContainer (DI simple) + ProductsViewModel

En este bloque se implementó una inyección de dependencias simple (sin Hilt)
mediante un AppContainer creado en la clase Application.

Se construyó el flujo completo de dependencias:
Room Database → DAO → Repository → ViewModel.

Se implementó:
- StockyApp: inicializa el AppContainer una sola vez al arrancar la app.
- AppContainer: crea la instancia única de StockyDatabase y expone ProductRepository.
- ProductsViewModel: consume el Repository, combina Flows y expone un StateFlow
  (ProductsUiState) para ser consumido por la UI.

Se agregó un ViewModelFactory para crear el ViewModel con dependencias sin
instanciarlo manualmente en la UI.

Resultado del bloque:
La app ya tiene un armado profesional de dependencias y un ViewModel listo
para alimentar pantallas Compose con estado reactivo.

### Bloque 1.6 – ProductsScreen (Compose + estado)

En este bloque se creó la primera pantalla real de la app: ProductsScreen.

Se conectó la UI con el ViewModel usando StateFlow:
- La pantalla observa uiState con collectAsStateWithLifecycle().
- La UI se dibuja como función del estado (recomposición automática).

Se implementó un layout básico:
- Tarjeta de métricas (total a costo y total a venta).
- Lista de productos con LazyColumn.
- Mensaje cuando la lista está vacía.
- Acciones MVP: agregar producto dummy (FAB) y eliminar producto.

Resultado del bloque:
Stocky ya muestra datos en pantalla y se valida la reactividad completa:
Room → DAO → Repository → ViewModel → UI Compose.

### Bloque 1.7 – Alta real de producto (AddProductDialog)

En este bloque se reemplazó el producto dummy por un alta real mediante un Dialog.

Se implementó un formulario en Compose con estado local para los inputs:
- Los TextFields trabajan con String y la conversión a Double/Int se hace al guardar.
- Se agregó validación mínima (nombre no vacío, números válidos, stock >= 0).
- Al confirmar, se crea un ProductEntity y se llama al ViewModel para insertar.

Se conectó el FAB de ProductsScreen para abrir el dialog y guardar productos reales.

Resultado del bloque:
La app permite cargar productos desde la UI y persistirlos en Room, manteniendo
reactividad completa hasta la pantalla.


### Bloque 1.8 – Editar producto (reutilización de formulario)

En este bloque se agregó la funcionalidad de edición de productos.

Se reutilizó el formulario del alta convirtiéndolo en un dialog genérico
(ProductFormDialog) que soporta dos modos:
- Agregar: initialProduct = null → se inserta un nuevo ProductEntity.
- Editar: initialProduct != null → se actualiza usando copy() manteniendo el mismo id.

Se agregó un botón “Editar” en cada item de la lista, que abre el dialog con los
valores precargados. Al guardar cambios se llama a update().

Resultado del bloque:
Se puede crear y editar productos reutilizando UI y manteniendo consistencia con Room.

### Bloque 1.9 – Stock bajo + filtro

En este bloque se implementó la detección y visualización de productos con stock bajo.

Cambios realizados:

- Se agregó lowStockProducts al ProductsUiState.
- La lógica de stock bajo se calcula en el ViewModel:
  currentStock <= minimumStock.
- Se agregó un estado local (showOnlyLowStock) en ProductsScreen.
- La pantalla decide qué lista mostrar (products o lowStockProducts).
- Se agregó un botón para alternar el filtro.
- Los productos con stock bajo se marcan visualmente en la lista.

Resultado:

La app ahora permite detectar y filtrar productos con bajo stock,
mejorando su utilidad real para emprendedores.

### Bloque 2.0 – Estructura base del módulo Ventas

Se agregó la estructura de datos para registrar ventas y su detalle:

- SaleEntity: representa una venta (id, date como timestamp, total).
- SaleItemEntity: representa cada ítem vendido (saleId, productId, quantity, unitPrice).
- Relación 1 a muchos: Sale → SaleItem.
- ForeignKeys para integridad referencial y CASCADE al borrar una venta.
- Se actualizaron las entities de Room en StockyDatabase y se incrementó la versión.

Resultado: Room queda preparado para soportar ventas con múltiples productos.

### Bloque 2.1 – SaleDao + relación SaleWithItems

Se implementó lectura de ventas con sus ítems asociados usando Room relations.

- Se creó SaleWithItems (modelo de lectura) con:
    - @Embedded para incluir SaleEntity
    - @Relation para obtener la lista de SaleItemEntity por saleId
- Se creó SaleDao con:
    - observeSalesWithItems() usando @Transaction
    - insertSale() devolviendo el ID generado
    - insertSaleItems() para insertar ítems en batch
- Se agregó saleDao() a StockyDatabase.

Resultado: la app puede observar el historial de ventas incluyendo su detalle.

### Bloque 2.2 – SalesRepository + inserción transaccional

Se implementó SalesRepository para manejar operaciones compuestas del módulo Ventas.

- observeSalesWithItems(): expone el Flow de ventas con ítems.
- insertSaleWithItems(): inserta una venta completa (Sale + Items) en una transacción.
    1) Inserta SaleEntity y obtiene el saleId generado.
    2) Construye SaleItemEntity con el saleId.
    3) Inserta los ítems en batch.
- Se definió NewSaleItem como modelo de entrada para crear ventas sin acoplar a Entities.

Resultado: la app puede persistir ventas con múltiples productos de forma atómica y escalable.

### Bloque 2.3 – Registrar venta y descontar stock (transacción)

Se implementó el caso de uso central del MVP: registrar una venta y actualizar stock de forma atómica.

- Se agregaron métodos en ProductDao:
    - getById(productId) para validar stock.
    - updateStock(productId, newStock) para descontar existencias.
- En SalesRepository se creó registerSale():
    - Calcula total desde los ítems (unitPrice * quantity).
    - Inserta SaleEntity y obtiene saleId.
    - Inserta SaleItemEntity asociados.
    - Descuenta stock de productos.
    - Todo dentro de database.withTransaction (todo o nada).
- Se agregó InsufficientStockException para señalar stock insuficiente.

Resultado:
Las ventas se guardan con consistencia y el inventario se actualiza automáticamente.

### Bloque 2.4 – UI Nueva Venta (1 producto) + manejo de errores

Se implementó una pantalla mínima para registrar ventas (MVP):

- NewSaleViewModel:
    - Observa lista de productos desde ProductRepository.
    - Permite seleccionar producto y cargar cantidad.
    - Llama a SalesRepository.registerSale() con un solo ítem.
    - Maneja error InsufficientStockException mostrando mensaje.
- NewSaleScreen:
    - Dropdown de productos (ExposedDropdownMenuBox).
    - Input de cantidad.
    - Botón “Registrar venta” con estado isSaving.

Resultado:
Ya se puede registrar una venta real y verificar el descuento de stock.

### Bloque 2.5 – Nueva venta con carrito (múltiples productos) + total en vivo

Se evolucionó la pantalla Nueva Venta para soportar múltiples productos por venta.

- Se creó CartItemUi como modelo de UI para representar items del carrito.
- NewSaleUiState ahora incluye:
    - cart: lista de items
    - total derivado (sumatoria de subtotales)
- NewSaleViewModel agrega operaciones:
    - addToCart(): agrega/actualiza cantidad si el producto ya existe en carrito
    - removeFromCart(): elimina item del carrito
    - registerSale(): registra venta usando todos los items del carrito
- La UI:
    - permite agregar productos al carrito
    - muestra items, subtotales y total
    - registra la venta completa

Resultado: ya se pueden registrar ventas reales con múltiples productos.

### Bloque 3.0 – Navigation Compose (base)

Se incorporó Navigation Compose para navegar entre pantallas en Stocky.

- Se agregaron rutas (Routes).
- Se creó StockyNavGraph con NavHost y composable destinations.
- ProductsScreen expone un callback onNewSaleClick para navegar sin acoplarse a NavController.
- MainActivity aloja el NavHost y provee ViewModels mediante factories.

Resultado: se puede probar NewSaleScreen y escalar a nuevas pantallas (historial, detalle, etc.).

### Bloque 3.1 – Historial de ventas + filtro por fecha (base)

Se implementó el módulo de historial:

- SaleDao agrega query para obtener ventas entre fechas con sus items.
- SalesRepository expone observeSalesBetween(from, to).
- Se creó SalesHistoryViewModel y SalesHistoryUiState.
- Se mapea SaleWithItems a un modelo de resumen (SaleSummaryUi).
- Se creó SalesHistoryScreen para listar ventas (fecha, total, conteos).
- Se agregó ruta de navegación a historial.

Resultado: Stocky permite visualizar ventas registradas y sentar base para filtros por fecha.

### Bloque 3.2 – Navegación completa entre pantallas

Se completó la navegación entre pantallas principales:

- Se agregó la ruta SALES_HISTORY.
- StockyNavGraph define 3 destinos: Products, New Sale y Sales History.
- Products muestra acciones en TopAppBar para navegar a Nueva venta y Ventas.
- NewSaleScreen y SalesHistoryScreen agregan botón de back en TopAppBar usando popBackStack().
- MainActivity provee los ViewModels necesarios al NavGraph.

Resultado: el flujo de navegación del MVP queda funcional y listo para crecer.

### Fix – Historial no mostraba ventas

Se corrigió la condición de renderizado en SalesHistoryScreen:
- El if estaba invertido (isNotEmpty mostraba "No hay ventas").
- Se cambió a isEmpty para mostrar el mensaje solo cuando no hay ventas.
### Bloque 3.3 – Filtro por fecha en historial

- Se implementó filtro por rango de fechas usando query SQL (BETWEEN) en SaleDao.
- SalesHistoryViewModel maneja el rango (from/to) y observa cambios de forma reactiva (flatMapLatest).
- En UI se agregaron DatePickers para seleccionar “Desde” y “Hasta”.
- Para incluir el día completo, se normaliza:
    - from = inicio del día
    - to = fin del día

Resultado: el historial permite filtrar ventas por fechas sin perder ventas del día por diferencias de hora.
