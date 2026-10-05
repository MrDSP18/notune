package echo.music.iad1tya.notune.intelligence.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import echo.music.iad1tya.notune.intelligence.knowledge.CastMember
import echo.music.iad1tya.notune.intelligence.knowledge.MovieDetails
import echo.music.iad1tya.notune.intelligence.knowledge.PersonDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    movieId: String,
    navController: NavController,
    viewModel: KnowledgeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.movieState.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetails(movieId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie & Soundtrack", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = uiState) {
                is KnowledgeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is KnowledgeUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
                is KnowledgeUiState.Success -> {
                    val movie = state.data
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Movie Header Card
                        item {
                            MovieHeroCard(movie = movie)
                        }

                        // Description
                        if (movie.description != null) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Synopsis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(movie.description, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }

                        // Lead Cast Section (Hero, Heroine, Lead Actors)
                        if (movie.leadActors.isNotEmpty() || movie.leadActresses.isNotEmpty() || movie.cast.isNotEmpty()) {
                            item {
                                MovieCastSection(
                                    leadActors = movie.leadActors,
                                    leadActresses = movie.leadActresses,
                                    cast = movie.cast,
                                    onPersonClick = { personId ->
                                        navController.navigate("person/$personId")
                                    }
                                )
                            }
                        }

                        // Key Crew (Directors, Music Directors, Producers)
                        item {
                            MovieCrewSection(
                                directors = movie.directors,
                                musicDirectors = movie.musicDirectors,
                                producers = movie.producers,
                                onPersonClick = { personId ->
                                    navController.navigate("person/$personId")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieHeroCard(movie: MovieDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (movie.heroBackdropArtwork?.url != null) {
                    AsyncImage(
                        model = movie.heroBackdropArtwork.url,
                        contentDescription = movie.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (movie.year != null) {
                    AssistChip(onClick = {}, label = { Text("${movie.year}") })
                }
                if (movie.language != null) {
                    AssistChip(onClick = {}, label = { Text(movie.language) })
                }
            }
        }
    }
}

@Composable
fun MovieCastSection(
    leadActors: List<PersonDetails>,
    leadActresses: List<PersonDetails>,
    cast: List<CastMember>,
    onPersonClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Lead Cast & Performers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            if (leadActors.isNotEmpty()) {
                Text("Hero / Lead Actor", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    leadActors.forEach { actor ->
                        PersonChip(person = actor, role = "Hero / Lead Actor", onClick = { onPersonClick(actor.personId) })
                    }
                }
            }

            if (leadActresses.isNotEmpty()) {
                Text("Heroine / Lead Actress", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    leadActresses.forEach { actress ->
                        PersonChip(person = actress, role = "Heroine / Lead Actress", onClick = { onPersonClick(actress.personId) })
                    }
                }
            }

            if (cast.isNotEmpty()) {
                Text("Cast Members", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    cast.forEach { member ->
                        PersonChip(person = member.person, role = member.role.name, onClick = { onPersonClick(member.person.personId) })
                    }
                }
            }

            if (leadActors.isEmpty() && leadActresses.isEmpty() && cast.isEmpty()) {
                Text("Information unavailable", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun MovieCrewSection(
    directors: List<PersonDetails>,
    musicDirectors: List<PersonDetails>,
    producers: List<PersonDetails>,
    onPersonClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Filmmakers & Music Directors", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            if (directors.isNotEmpty()) {
                Text("Director", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    directors.forEach { director ->
                        PersonChip(person = director, role = "Director", onClick = { onPersonClick(director.personId) })
                    }
                }
            }

            if (musicDirectors.isNotEmpty()) {
                Text("Music Director / Composer", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    musicDirectors.forEach { composer ->
                        PersonChip(person = composer, role = "Music Director", onClick = { onPersonClick(composer.personId) })
                    }
                }
            }

            if (producers.isNotEmpty()) {
                Text("Producer", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    producers.forEach { producer ->
                        PersonChip(person = producer, role = "Producer", onClick = { onPersonClick(producer.personId) })
                    }
                }
            }

            if (directors.isEmpty() && musicDirectors.isEmpty() && producers.isEmpty()) {
                Text("Information unavailable", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun PersonChip(person: PersonDetails, role: String, onClick: () -> Unit) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = { Text(person.name) },
        leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = role, modifier = Modifier.size(16.dp))
        }
    )
}
