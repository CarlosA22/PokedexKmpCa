package com.treino.pokedexkmpca.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.treino.pokedexkmpca.domain.model.Pokemon
import com.treino.pokedexkmpca.presentation.viewmodel.PokemonDetailUiState
import com.treino.pokedexkmpca.util.rememberImageStorage
import com.treino.pokedexkmpca.util.rememberHardwareManager
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.launch

@Composable
fun PokemonDetailScreen(
    uiState: PokemonDetailUiState,
    onTeamClick: (Pokemon, String?, Double?, Double?, String?) -> Unit,
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        when (uiState) {
            is PokemonDetailUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is PokemonDetailUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is PokemonDetailUiState.Success -> {
                PokemonDetailContent(uiState.pokemon, onTeamClick, onBackClick)
            }
        }
    }
}

@Composable
private fun PokemonDetailContent(
    pokemon: Pokemon,
    onTeamClick: (Pokemon, String?, Double?, Double?, String?) -> Unit,
    onBackClick: () -> Unit
) {
    val typeColor = getPokemonTypeColor(pokemon.types.firstOrNull() ?: "normal")
    val scope = rememberCoroutineScope()
    val imageStorage = rememberImageStorage()

    // Permissions
    val permissionsControllerFactory = rememberPermissionsControllerFactory()
    val permissionsController = remember(permissionsControllerFactory) { permissionsControllerFactory.createPermissionsController() }
    BindEffect(permissionsController)

    val hardwareManager = rememberHardwareManager { result ->
        scope.launch {
            val path = result.photoBytes?.let { imageStorage.saveImage(it) }
            onTeamClick(
                pokemon, 
                "Capturado via GPS", 
                result.latitude, 
                result.longitude, 
                path
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(typeColor.copy(alpha = 0.8f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
            }

            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 40.dp)
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = pokemon.name.capitalizePokemonName(),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            )
            Text(
                text = pokemon.id.formatPokemonNumber(),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pokemon.types.forEach { type ->
                    SmallTypeChip(type)
                }
            }

            // FOTO CAPTURADA (REQUISITO DO PDF)
            if (pokemon.isFavorite && pokemon.photoPath != null) {
                Text(
                    text = "Foto da Captura",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp).fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                AsyncImage(
                    model = pokemon.photoPath,
                    contentDescription = "Foto da captura",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            }

            // LOCALIZAÇÃO (REQUISITO DO PDF)
            if (pokemon.isFavorite && pokemon.latitude != null && pokemon.longitude != null) {
                Card(
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = typeColor.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = typeColor)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Coordenadas de Captura", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("Lat: ${pokemon.latitude}, Lon: ${pokemon.longitude}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // BOTÃO ADICIONAR AO TIME (REQUISITO DO PDF)
            Button(
                onClick = { 
                    if (pokemon.isFavorite) {
                        onTeamClick(pokemon, null, null, null, null)
                    } else {
                        scope.launch {
                            try {
                                permissionsController.providePermission(Permission.LOCATION)
                                permissionsController.providePermission(Permission.CAMERA)
                                hardwareManager.capture()
                            } catch (e: Exception) {
                                // Tratar negação de permissão de forma amigável
                            }
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pokemon.isFavorite) Color.Red else typeColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    if (pokemon.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(if (pokemon.isFavorite) "Remover do Time" else "Capturar e Adicionar ao Time")
            }

            Text(
                text = pokemon.description,
                modifier = Modifier.padding(top = 24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoItem(label = "PESO", value = "${pokemon.weight / 10.0} kg")
                InfoItem(label = "ALTURA", value = "${pokemon.height / 10.0} m")
            }

            Text(
                text = "Stats",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 16.dp)
            )

            pokemon.stats.forEach { stat ->
                StatBar(stat.name, stat.value, typeColor)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
fun StatBar(name: String, value: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name.uppercase(),
            modifier = Modifier.width(100.dp),
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Text(
            text = value.toString().padStart(3, '0'),
            modifier = Modifier.width(40.dp),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        LinearProgressIndicator(
            progress = { value / 255f },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
