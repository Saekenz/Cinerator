document.getElementById('load-movie-by-country').addEventListener('click', fetchMovieData);

async function fetchMovieData() {
    const movieCountry = document.getElementById('movie-country').value;
    if (!movieCountry) return alert('Please enter a country');

    const url = `http://localhost:8080/movies/country/${encodeURIComponent(movieCountry)}`;

    await fetch(url)
        .then(response => response.json())
        .then(data => displayMovieData(data))
        .catch(error => console.error('Error fetching movie data:', error));

    document.getElementById("movie-country").value = ""
}
