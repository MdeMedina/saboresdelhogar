package com.example.saboresdehogar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.saboresdehogar.model.menu.CategoryType
import com.example.saboresdehogar.model.menu.MenuItem
import com.example.saboresdehogar.ui.theme.SaboresDeHogarTheme
import com.example.saboresdehogar.viewmodel.CartViewModel
import com.example.saboresdehogar.viewmodel.MenuViewModel
// Simulación de ViewModels para el Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saboresdehogar.viewmodel.ViewModelFactory

@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String?,
    menuViewModel: MenuViewModel,
    cartViewModel: CartViewModel
) {
    var item by remember { mutableStateOf<MenuItem?>(null) }

    // Cargar los detalles del item
    LaunchedEffect(productId) {
        if (productId != null) {
            item = menuViewModel.getItemById(productId)
        }
    }

    // UI
    if (item != null) {
        val currentItem = item!! // No será nulo aquí
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Imagen del producto
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentItem.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = currentItem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            // Contenido
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Nombre
                Text(
                    text = currentItem.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Precio
                Text(
                    text = "CLP $${String.format("%,.0f", currentItem.price)}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Descripción
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentItem.description, // Descripción "extendida"
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4 // Más espaciado
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Botón
                Button(
                    onClick = {
                        cartViewModel.addItem(currentItem)
                        // Opcional: Mostrar un mensaje o navegar al carrito
                        // navController.navigate(Screen.Cart.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Agregar al Carrito", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    } else {
        // Manejo de estado nulo o de carga
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (productId == null) {
                Text("Error: ID de producto no válido.")
            } else {
                // Este texto solo se verá si getItemById es asíncrono.
                // Como el tuyo es síncrono, la carga es casi instantánea.
                CircularProgressIndicator()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
    // Simulación para el Preview
    val context = LocalContext.current
    val factory = ViewModelFactory(context)
    val menuViewModel: MenuViewModel = viewModel(factory = factory)
    val cartViewModel: CartViewModel = viewModel(factory = factory)

    // Un item de ejemplo
    val previewItem = MenuItem(
        id = "plato008",
        name = "Paila Marina",
        description = "Exquisita sopa de mariscos frescos del Pacífico con un toque de vino blanco y cilantro. Una experiencia gastronómica única que te transportará a la costa chilena.",
        price = 9500.0,
        category = CategoryType.PLATOS_PRINCIPALES,
        imageUrl = "https://via.placeholder.com/400x300/C1272D/FFFFFF?text=Paila+Marina",
        isVegetarian = false,
        isAvailable = true
    )

    // Simulamos que el viewModel ya tiene el item
    menuViewModel.loadMenu() // Carga los items (aunque usaremos el de arriba)

    SaboresDeHogarTheme {
        ProductDetailScreen(
            navController = rememberNavController(),
            productId = "plato008", // ID de ejemplo
            menuViewModel = menuViewModel,
            cartViewModel = cartViewModel
        )
    }
}