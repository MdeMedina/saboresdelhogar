package com.example.saboresdehogar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saboresdehogar.components.Screen
import com.example.saboresdehogar.model.menu.CategoryType
import com.example.saboresdehogar.screens.components.ProductCard
import com.example.saboresdehogar.viewmodel.CartViewModel
import com.example.saboresdehogar.viewmodel.MenuViewModel
import com.example.saboresdehogar.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    navController: NavController,
    menuViewModel: MenuViewModel,
    cartViewModel: CartViewModel
) {
    // Observamos los datos del ViewModel
    val categories by menuViewModel.menuCategories.observeAsState(emptyList())
    val menuItems by menuViewModel.menuItems.observeAsState(emptyList())
    val selectedCategory by menuViewModel.selectedCategory.observeAsState()

    // Cargamos el menú cuando la pantalla se inicia
    LaunchedEffect(Unit) {
        menuViewModel.loadMenu()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (categories.isNotEmpty()) {
            // --- Barra de Pestañas (Tabs) ---
            ScrollableTabRow(
                selectedTabIndex = selectedCategory?.ordinal ?: 0,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                // Tab para "Todos"
                Tab(
                    selected = selectedCategory == null,
                    onClick = { menuViewModel.filterByCategory(null) },
                    text = { Text("Todos", fontWeight = if (selectedCategory == null) FontWeight.Bold else FontWeight.Normal) }
                )
                // Tabs por categoría
                categories.forEach { category ->
                    Tab(
                        selected = selectedCategory == category.type,
                        onClick = { menuViewModel.filterByCategory(category.type) },
                        text = {
                            Text(
                                text = category.displayName,
                                fontWeight = if (selectedCategory == category.type) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // --- Lista de Productos ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(menuItems) { item ->
                    ProductCard(
                        item = item,
                        onCardClick = { productId ->
                            // Navegar a la pantalla de detalle
                            navController.navigate(Screen.ProductDetail.createRoute(productId))
                        },
                        onAddToCartClick = {
                            // Añadir al carrito
                            cartViewModel.addItem(it)
                        }
                    )
                }
            }
        } else {
            // Podríamos mostrar un indicador de carga aquí
        }
    }
}