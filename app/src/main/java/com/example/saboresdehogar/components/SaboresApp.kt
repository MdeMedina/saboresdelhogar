package com.example.saboresdehogar.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.example.saboresdehogar.screens.CartScreen
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.saboresdehogar.R
// --- IMPORTAMOS LA PANTALLA CREADA ---
import com.example.saboresdehogar.screens.LoginScreen
import com.example.saboresdehogar.screens.RegisterScreen
import com.example.saboresdehogar.screens.CatalogScreen
import com.example.saboresdehogar.screens.ProductDetailScreen
// -------------------------------------
import com.example.saboresdehogar.viewmodel.AuthViewModel
import com.example.saboresdehogar.viewmodel.CartViewModel
import com.example.saboresdehogar.viewmodel.ViewModelFactory
import com.example.saboresdehogar.viewmodel.MenuViewModel
// --- Definición de TODAS las rutas de la app ---
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home") // La pantalla del Moai
    object Catalog : Screen("catalog") // El menú de productos
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Cart : Screen("cart")
    object CheckoutSuccess : Screen("checkout_success")
    object CheckoutFail : Screen("checkout_fail")

    // Rutas del Back Office
    object AdminDashboard : Screen("admin_dashboard")
    object AdminProductList : Screen("admin_product_list")
    object AdminAddProduct : Screen("admin_add_product")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaboresApp() {
    val navController = rememberNavController()
    val factory = ViewModelFactory(LocalContext.current)
    val authViewModel: AuthViewModel = viewModel(factory = factory)

    // Observamos el estado de autenticación
    val authState by authViewModel.authState.observeAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Decidimos qué pantalla mostrar
    Scaffold(
        topBar = {
            // No mostrar TopBar en Splash, Login o Register
            if (currentRoute != Screen.Splash.route &&
                currentRoute != Screen.Login.route &&
                currentRoute != Screen.Register.route
            ) {
                SaboresTopAppBar(
                    currentRoute = currentRoute,
                    onMenuClick = { /* TODO: Abrir Drawer */ },
                    onBackClick = { navController.navigateUp() }
                )
            }
        },
        floatingActionButton = {
            // Mostrar FAB solo en pantallas de la tienda
            if (currentRoute == Screen.Catalog.route || currentRoute?.startsWith("product_detail") == true) {
                val cartViewModel: CartViewModel = viewModel(factory = factory)
                val itemCount by cartViewModel.itemCount.observeAsState(0)
                CartFab(itemCount = itemCount, onClick = {
                    navController.navigate(Screen.Cart.route)
                })
            }
        }
    ) { innerPadding ->

        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            authState = authState,
            factory = factory // Pasamos el factory al NavHost
        )
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authState: com.example.saboresdehogar.viewmodel.AuthState?,
    factory: ViewModelFactory // Recibimos el factory
) {
    // Definimos la ruta inicial basada en el estado de autenticación
    val startDestination = when (authState) {
        is com.example.saboresdehogar.viewmodel.AuthState.Authenticated -> Screen.Catalog.route
        is com.example.saboresdehogar.viewmodel.AuthState.Unauthenticated -> Screen.Login.route
        else -> Screen.Splash.route // Muestra Splash mientras carga
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- Flujo de Autenticación ---
        composable(Screen.Splash.route) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // Pantalla de carga simple
            }
        }

        composable(Screen.Login.route) {
            // --- ¡AQUÍ ESTÁ EL CAMBIO! ---
            // Ya no es un placeholder
            val authViewModel: AuthViewModel = viewModel(factory = factory)
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.Register.route) {
            // --- ¡AQUÍ ESTÁ EL CAMBIO! ---
            val authViewModel: AuthViewModel = viewModel(factory = factory)
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // --- Flujo Principal (Tienda) ---
        composable(Screen.Home.route) {
            HomeScreen() // Tu pantalla de inicio con fondo Moai
        }

        composable(Screen.Catalog.route) {
            val menuViewModel: MenuViewModel = viewModel(factory = factory)
            val cartViewModel: CartViewModel = viewModel(factory = factory)
            CatalogScreen(
                navController = navController,
                menuViewModel = menuViewModel,
                cartViewModel = cartViewModel
            )
        }

        composable(Screen.Cart.route) {
            // --- ¡AQUÍ ESTÁ EL CAMBIO! ---
            val cartViewModel: CartViewModel = viewModel(factory = factory)
            CartScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }

        composable(Screen.ProductDetail.route) { backStackEntry ->
            // --- ¡AQUÍ ESTÁ EL CAMBIO! ---
            val productId = backStackEntry.arguments?.getString("productId")
            val menuViewModel: MenuViewModel = viewModel(factory = factory)
            val cartViewModel: CartViewModel = viewModel(factory = factory)

            ProductDetailScreen(
                navController = navController,
                productId = productId,
                menuViewModel = menuViewModel,
                cartViewModel = cartViewModel
            )
        }

        composable(Screen.CheckoutSuccess.route) {
            // Aquí irá tu Pantalla de Compra Exitosa
            PlaceholderScreen(text = "Pantalla COMPRA EXITOSA")
        }

        composable(Screen.CheckoutFail.route) {
            // Aquí irá tu Pantalla de Compra Rechazada
            PlaceholderScreen(text = "Pantalla COMPRA RECHAZADA")
        }

        // --- Flujo de Back Office ---
        composable(Screen.AdminDashboard.route) {
            // Aquí irá tu Pantalla de Back Office
            PlaceholderScreen(text = "Pantalla ADMIN DASHBOARD")
        }

        composable(Screen.AdminProductList.route) {
            // Aquí irá tu Lista de Productos en Back Office
            PlaceholderScreen(text = "Pantalla ADMIN (Lista Productos)")
        }

        composable(Screen.AdminAddProduct.route) {
            // Aquí irá tu Pantalla para Agregar Producto
            PlaceholderScreen(text = "Pantalla ADMIN (Agregar Producto)")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaboresTopAppBar(
    currentRoute: String?,
    onMenuClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Títulos dinámicos basados en la ruta
    val title = when (currentRoute) {
        Screen.Catalog.route -> "Menú"
        Screen.Cart.route -> "Tu Carrito"
        Screen.ProductDetail.route -> "Detalle del Producto"
        Screen.AdminProductList.route -> "Gestionar Productos"
        Screen.AdminAddProduct.route -> "Agregar Producto"
        else -> ""
    }

    // Mostrar flecha de "Volver" en pantallas secundarias
    val showBackButton = currentRoute != Screen.Catalog.route && currentRoute != Screen.Home.route

    TopAppBar(
        title = {
            if (title.isNotEmpty()) {
                Text(title, fontWeight = FontWeight.Bold)
            } else {
                // Logo en la pantalla principal del catálogo
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // CAMBIA ESTO por tu logo
                    contentDescription = "Logo Sabores de Hogar",
                    modifier = Modifier.height(40.dp)
                )
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            } else {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Abrir menú"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun CartFab(
    itemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
    ) {
        BadgedBox(
            badge = {
                if (itemCount > 0) {
                    Badge { Text(text = "$itemCount") }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Ver Carrito"
            )
        }
    }
}

// Composable temporal para rellenar las pantallas
@Composable
fun PlaceholderScreen(text: String, onNavigate: (() -> Unit)? = null) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text, style = MaterialTheme.typography.headlineSmall)
            if (onNavigate != null) {
                Spacer(Modifier.height(20.dp))
                Button(onClick = onNavigate) {
                    Text("Navegar (Test)")
                }
            }
        }
    }
}