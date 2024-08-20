document.getElementById('load-movie-by-director').addEventListener('click', fetchMovieData);

async function fetchMovieData() {
    const movieDirector = document.getElementById('movie-director').value;
    if (!movieDirector) return alert('Please enter a director');

    const url = `http://localhost:8080/movies/director/${encodeURIComponent(movieDirector)}`;

    await fetch(url)
        .then(response => response.json())
        .then(data => displayMovieData(data))
        .catch(error => console.error('Error fetching movie data:', error));

    document.getElementById("movie-director").value = ""
    document.title = "Movies by "+ movieDirector;
}