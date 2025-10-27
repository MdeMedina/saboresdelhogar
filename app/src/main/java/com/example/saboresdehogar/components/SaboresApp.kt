package com.example.saboresdehogar.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.saboresdehogar.R
import com.example.saboresdehogar.model.user.UserRole
import com.example.saboresdehogar.screens.* // Importa todas las pantallas
import com.example.saboresdehogar.viewmodel.AuthState
import com.example.saboresdehogar.viewmodel.AuthViewModel
import com.example.saboresdehogar.viewmodel.CartViewModel
import com.example.saboresdehogar.viewmodel.MenuViewModel
import com.example.saboresdehogar.viewmodel.OrderViewModel
import com.example.saboresdehogar.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay

// --- Definición de TODAS las rutas de la app ---
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Catalog : Screen("catalog")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Cart : Screen("cart")
    object CheckoutSuccess : Screen("checkout_success")
    object CheckoutFail : Screen("checkout_fail")

    // Rutas del Back Office
    object AdminDashboard : Screen("admin_dashboard") // Lo dejaremos, pero sin uso
    object AdminProductList : Screen("admin_product_list")
    object AdminAddProduct : Screen("admin_add_product")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaboresApp() {
    val navController = rememberNavController()
    val factory = ViewModelFactory(LocalContext.current)
    val authViewModel: AuthViewModel = viewModel(factory = factory)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            // No mostrar TopBar en Splash, Login, Register o pantallas de Admin
            if (currentRoute != Screen.Splash.route &&
                currentRoute != Screen.Login.route &&
                currentRoute != Screen.Register.route &&
                currentRoute?.startsWith("admin_") == false
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
            authViewModel = authViewModel, // Pasamos el VM
            factory = factory
        )
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    factory: ViewModelFactory
) {
    val authState by authViewModel.authState.observeAsState()
    val currentUser by authViewModel.currentUser.observeAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route, // <-- CAMBIO A SPLASH
        modifier = modifier
    ) {
        // --- Pantalla de Ruteo ---
        composable(Screen.Splash.route) {
            LaunchedEffect(authState, currentUser) {
                delay(1000) // Un pequeño delay para que el estado se asiente
                val route = when (authState) {
                    is AuthState.Authenticated -> {
                        if (currentUser?.role == UserRole.ADMIN) {
                            Screen.AdminProductList.route // <-- ADMIN VA AQUÍ
                        } else {
                            Screen.Catalog.route // <-- CUSTOMER VA AQUÍ
                        }
                    }
                    is AuthState.Unauthenticated -> Screen.Login.route
                    else -> null // Sigue en Splash si está cargando
                }

                if (route != null) {
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // Pantalla de carga
            }
        }

        // --- Flujo de Autenticación ---
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // --- Flujo Principal (Tienda) ---
        composable(Screen.Home.route) {
            HomeScreen()
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

        composable(Screen.ProductDetail.route) { backStackEntry ->
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

        composable(Screen.Cart.route) {
            val cartViewModel: CartViewModel = viewModel(factory = factory)
            val orderViewModel: OrderViewModel = viewModel(factory = factory)
            CartScreen(
                navController = navController,
                authViewModel = authViewModel,
                cartViewModel = cartViewModel,
                orderViewModel = orderViewModel
            )
        }

        composable(Screen.CheckoutSuccess.route) {
            CheckoutSuccessScreen(navController = navController)
        }

        composable(Screen.CheckoutFail.route) {
            CheckoutFailScreen(navController = navController)
        }

        // --- Flujo de Back Office ---
        composable(Screen.AdminProductList.route) {
            // --- ¡AQUÍ ESTÁ EL CAMBIO! ---
            val menuViewModel: MenuViewModel = viewModel(factory = factory)
            AdminProductListScreen(
                navController = navController,
                authViewModel = authViewModel,
                menuViewModel = menuViewModel
            )
        }

        composable(Screen.AdminAddProduct.route) {
            // --- ¡AQUÍ ESTÁ EL CAMBIO! ---
            AdminAddProductScreen(navController = navController)
        }
    }
}

// ... (El resto de tu SaboresTopAppBar, CartFab, y PlaceholderScreen) ...
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

    val showBackButton = currentRoute != Screen.Catalog.route && currentRoute != Screen.Home.route

    TopAppBar(
        title = {
            if (title.isNotEmpty()) {
                Text(title, fontWeight = FontWeight.Bold)
            } else {
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