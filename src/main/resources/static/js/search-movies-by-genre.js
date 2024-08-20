document.getElementById('load-movie-by-genre').addEventListener('click', fetchMovieData);

async function fetchMovieData() {
    const movieGenre = document.getElementById('movie-genre').value;
    if (!movieGenre) return alert('Please enter a genre');

    const url = `http://localhost:8080/movies/genre/${encodeURIComponent(movieGenre)}`;

    await fetch(url)
        .then(response => response.json())
        .then(data => displayMovieData(data))
        .catch(error => console.error('Error fetching movie data:', error));

    document.getElementById("movie-genre").value = ""
    document.title = movieGenre + " movies";
}
