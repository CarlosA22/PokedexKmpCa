# Integração Nativa: Câmera e Geolocalização

Evolução da aplicação Pokédex Multiplatform para integrar recursos de hardware (GPS e Câmera) ao capturar Pokémon para o time, com persistência local dos novos dados.

## Alterações Propostas

### Camada de Domínio

#### [Pokemon.kt](file:///Z:/PokedexKmpCa/composeApp/src/commonMain/kotlin/com/treino/pokedexkmpca/domain/model/Pokemon.kt)
- Adicionados campos `latitude: Double?`, `longitude: Double?` e `photoPath: String?` ao modelo de domínio.

#### [PokemonRepository.kt](file:///Z:/PokedexKmpCa/composeApp/src/commonMain/kotlin/com/treino/pokedexkmpca/domain/repository/PokemonRepository.kt)
- Atualizada a assinatura de `toggleFavorite` para aceitar as novas informações de captura.

---

### Camada de Dados (Persistência)

#### [FavoritePokemonEntity.kt](file:///Z:/PokedexKmpCa/composeApp/src/commonMain/kotlin/com/treino/pokedexkmpca/data/local/entity/FavoritePokemonEntity.kt)
- Adicionadas colunas `latitude`, `longitude` e `photoPath` na tabela `favorite_pokemons`.

#### [PokemonDatabase.kt](file:///Z:/PokedexKmpCa/composeApp/src/commonMain/kotlin/com/treino/pokedexkmpca/data/local/PokemonDatabase.kt)
- Incrementada a versão do banco de dados para `2`.
- Ativada a migração destrutiva (`fallbackToDestructiveMigration`) para recriação do banco conforme opção do requisito.

#### [PokemonRepositoryImpl.kt](file:///Z:/PokedexKmpCa/composeApp/src/commonMain/kotlin/com/treino/pokedexkmpca/data/repository/PokemonRepositoryImpl.kt)
- Implementada a lógica de salvar e recuperar os novos campos do banco de dados.
- Atualizado `getPokemonById` para mesclar dados remotos com dados locais de captura (foto e coordenadas).

---

### Integração de Hardware (Native Multiplatform)

#### [NEW] [HardwareManager.kt](file:///Z:/PokedexKmpCa/composeApp/src/commonMain/kotlin/com/treino/pokedexkmpca/util/HardwareManager.kt)
- Interface comum para captura de foto e localização via GPS.

#### [NEW] [HardwareManager.android.kt](file:///Z:/PokedexKmpCa/composeApp/src/androidMain/kotlin/com/treino/pokedexkmpca/util/HardwareManager.android.kt)
- Implementação nativa Android usando `ActivityResultLauncher` (Câmera) e `LocationManager` (GPS).

#### [NEW] [HardwareManager.ios.kt](file:///Z:/PokedexKmpCa/composeApp/src/iosMain/kotlin/com/treino/pokedexkmpca/util/HardwareManager.ios.kt)
- Implementação nativa iOS usando `UIImagePickerController` e `CLLocationManager`.

#### [NEW] [ImageStorage.kt](file:///Z:/PokedexKmpCa/composeApp/src/commonMain/kotlin/com/treino/pokedexkmpca/util/ImageStorage.kt)
- Utilitário para salvar `ByteArray` da foto no armazenamento interno do dispositivo (`filesDir` no Android, `Documents` no iOS).

---

### Interface de Usuário (UI)

#### [PokemonDetailScreen.kt](file:///Z:/PokedexKmpCa/composeApp/src/commonMain/kotlin/com/treino/pokedexkmpca/ui/PokemonDetailScreen.kt)
- Integrada a biblioteca `moko-permissions` para solicitação de permissões em tempo real.
- Adicionado fluxo de captura (GPS + Câmera) ao clicar em "Adicionar ao Time".
- Implementada exibição da foto capturada e das coordenadas geográficas na tela de detalhes para Pokémon que já estão no time.
- Removido o diálogo manual de localização da M2, automatizando o processo conforme requisito.

---

### Configurações de Projeto e Permissões

#### [libs.versions.toml](file:///Z:/PokedexKmpCa/gradle/libs.versions.toml)
- Adicionada dependência `dev.icerock.moko:permissions-compose`.

#### [AndroidManifest.xml](file:///Z:/PokedexKmpCa/composeApp/src/androidMain/AndroidManifest.xml)
- Adicionadas permissões `CAMERA`, `ACCESS_FINE_LOCATION` e `ACCESS_COARSE_LOCATION`.

#### [Info.plist](file:///Z:/PokedexKmpCa/iosApp/iosApp/Info.plist)
- Adicionadas chaves `NSCameraUsageDescription` e `NSLocationWhenInUseUsageDescription`.

## Plano de Verificação

### Testes Automatizados
- Executar build do projeto: `./gradlew :composeApp:compileDebugKotlinAndroid`

### Verificação Manual
1. Abrir a Pokédex e selecionar um Pokémon.
2. Clicar em "Capturar e Adicionar ao Time".
3. Verificar se as permissões de Câmera e Localização são solicitadas.
4. Tirar a foto e confirmar.
5. Verificar se o Pokémon foi salvo no time.
6. Acessar a tela do time e ver os detalhes: a foto tirada e as coordenadas devem estar visíveis.
