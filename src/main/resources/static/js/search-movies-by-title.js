document.getElementById('load-movie-by-title').addEventListener('click', fetchMovieData);

async function fetchMovieData() {
    const movieTitle = document.getElementById('movie-title').value;
    if (!movieTitle) return alert('Please enter a movie title');

    let url;
    if (checkIfTitleIsImdbId(movieTitle)) {
        url = `http://localhost:8080/movies/imdb_id/${encodeURIComponent(movieTitle)}`;
    } else {
        url = `http://localhost:8080/movies/title/${encodeURIComponent(movieTitle)}`;
    }

    await fetch(url)
        .then(response => response.json())
        .then(data => displayMovieData(data))
        .catch(error => console.error('Error fetching movie data:', error));

    document.getElementById("movie-title").value = ""
}

function checkIfTitleIsImdbId(title) {
    const regex = /^tt\d+$/;
    return regex.test(title);
}