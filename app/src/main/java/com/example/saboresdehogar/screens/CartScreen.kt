package com.example.saboresdehogar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saboresdehogar.components.Screen
import com.example.saboresdehogar.model.order.OrderType
import com.example.saboresdehogar.screens.components.CartItemRow
import com.example.saboresdehogar.ui.theme.SaboresDeHogarTheme
import com.example.saboresdehogar.viewmodel.AuthViewModel
import com.example.saboresdehogar.viewmodel.CartViewModel
import com.example.saboresdehogar.viewmodel.OrderViewModel
import com.example.saboresdehogar.viewmodel.ViewModelFactory

@Composable
fun CartScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel
) {
    val cart by cartViewModel.cart.observeAsState()
    val cartItems = cart?.items ?: emptyList()
    val subtotal = cart?.total ?: 0.0
    val iva = subtotal * 0.19 // 19% IVA
    val total = subtotal + iva

    // Observamos el estado del OrderViewModel
    val orderCreated by orderViewModel.orderCreated.observeAsState(false)
    val orderError by orderViewModel.orderError.observeAsState()

    // --- Lógica de Navegación ---
    LaunchedEffect(orderCreated) {
        if (orderCreated) {
            navController.navigate(Screen.CheckoutSuccess.route) {
                // Limpiamos la pila para que no pueda "volver" al carrito
                popUpTo(Screen.Catalog.route)
            }
            orderViewModel.clearOrderCreated() // Limpiamos el estado
        }
    }

    LaunchedEffect(orderError) {
        if (orderError != null) {
            navController.navigate(Screen.CheckoutFail.route)
            orderViewModel.clearError() // Limpiamos el estado
        }
    }

    // Obtenemos los datos del usuario logueado
    val currentUser by authViewModel.currentUser.observeAsState()
    val customerName = currentUser?.name ?: "Cliente"
    val customerPhone = currentUser?.phone ?: "Sin teléfono"
    val customerAddress = currentUser?.defaultAddress ?: "Sin dirección"

    Column(modifier = Modifier.fillMaxSize()) {
        if (cartItems.isEmpty()) {
            // --- Carrito Vacío ---
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tu carrito está vacío",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // --- Lista de Items ---
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            ) {
                items(cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onIncrement = { cartViewModel.incrementQuantity(it) },
                        onDecrement = { cartViewModel.decrementQuantity(it) },
                        onRemove = { cartViewModel.removeItem(it) }
                    )
                    Divider()
                }
            }

            // --- Resumen de Pago ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                PriceSummaryRow("Subtotal:", "CLP $${String.format("%,.0f", subtotal)}")
                PriceSummaryRow("IVA (19%):", "CLP $${String.format("%,.0f", iva)}")
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                PriceSummaryRow(
                    label = "Total:",
                    value = "CLP $${String.format("%,.0f", total)}",
                    isTotal = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón "Proceder al Pago"
                Button(
                    onClick = {
                        orderViewModel.createOrder(
                            customerName = customerName,
                            customerPhone = customerPhone,
                            orderType = OrderType.DELIVERY, // Asumimos Delivery
                            deliveryAddress = customerAddress,
                            notes = "Pedido desde App"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Proceder al Pago", fontSize = 16.sp)
                }

                // --- Botón de simulación de error ---
                OutlinedButton(
                    onClick = {
                        // Forzamos un error para probar la otra pantalla
                        orderViewModel.createOrder(
                            customerName = "", // Nombre vacío para forzar error
                            customerPhone = "",
                            orderType = OrderType.DELIVERY
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Simular Compra Rechazada")
                }
            }
        }
    }
}

@Composable
fun PriceSummaryRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = if (isTotal) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    val factory = ViewModelFactory(LocalContext.current)
    SaboresDeHogarTheme {
        CartScreen(
            navController = rememberNavController(),
            authViewModel = viewModel(factory = factory),
            cartViewModel = viewModel(factory = factory),
            orderViewModel = viewModel(factory = factory)
        )
    }
}