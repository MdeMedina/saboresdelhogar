package com.example.saboresdehogar.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp // <-- IMPORTANTE ASEGURARSE DE TENER ESTE
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.saboresdehogar.model.menu.MenuItem
import androidx.compose.material3.ButtonDefaults

@Composable
fun AdminProductRow(
    item: MenuItem
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(MaterialTheme.shapes.medium)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "CLP $${String.format("%,.0f", item.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Botones visuales (sin función)
            Row {
                OutlinedButton(onClick = { /* No-op */ }, enabled = false) {
                    Text("Editar")
                }
                Spacer(modifier = Modifier.width(8.dp))

                // --- INICIO DE LA CORRECCIÓN (HARDCODE) ---
                OutlinedButton(
                    onClick = { /* No-op */ },
                    enabled = false,
                    // 1. Creamos el borde manualmente con 1.dp
                    border = BorderStroke(
                        width = 1.dp, // <-- ¡CAMBIO AQUÍ! Usamos el valor real
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    ),
                    // 2. El color del texto (contenido)
                    colors = ButtonDefaults.outlinedButtonColors(
                        disabledContentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    )
                ) {
                    Text("Borrar")
                }
                // --- FIN DE LA CORRECCIÓN ---
            }
        }
    }
}