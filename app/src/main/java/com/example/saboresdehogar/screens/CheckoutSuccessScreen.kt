package com.example.saboresdehogar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.example.saboresdehogar.components.Screen
import com.example.saboresdehogar.ui.theme.SaboresDeHogarTheme

@Composable
fun CheckoutSuccessScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Éxito",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Compra Exitosa!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tu pedido ha sido registrado. Recibirás una confirmación pronto. ¡Gracias por tu preferencia!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Botón para seguir comprando (vuelve al catálogo)
        Button(
            onClick = {
                navController.navigate(Screen.Catalog.route) {
                    // Limpiamos la pila de navegación hasta el catálogo
                    popUpTo(Screen.Catalog.route) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seguir Comprando", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver al inicio
        TextButton(
            onClick = {
                navController.navigate(Screen.Home.route) {
                    // Limpiamos la pila de navegación
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                }
            }
        ) {
            Text("Volver al Inicio")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutSuccessScreenPreview() {
    SaboresDeHogarTheme {
        CheckoutSuccessScreen(navController = rememberNavController())
    }
}