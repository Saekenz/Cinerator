document.getElementById('load-movie-by-year').addEventListener('click', fetchMovieData);

async function fetchMovieData() {
    const movieYear = document.getElementById('movie-year').value;
    if (!movieYear) return alert('Please enter a year');

    const url = `http://localhost:8080/movies/year/${encodeURIComponent(movieYear)}`;

    await fetch(url)
        .then(response => response.json())
        .then(data => displayMovieData(data))
        .catch(error => console.error('Error fetching movie data:', error));

    document.getElementById("movie-year").value = ""
    document.title = "Movies from "+ movieYear;
}
