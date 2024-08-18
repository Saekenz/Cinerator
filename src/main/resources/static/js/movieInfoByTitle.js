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

function displayMovieData(movieData) {
    const container = document.getElementById('movie-details');
    container.innerHTML = ''

    const movies = Array.isArray(movieData._embedded?.movieList) ? movieData._embedded.movieList : [movieData];

    movies.forEach(movie => {
        const movieDiv = document.createElement('div');
        movieDiv.classList.add('movie');

        // Movie Title
        const title = document.createElement('div');
        title.classList.add('movie-title');
        title.textContent = movie.title;
        movieDiv.appendChild(title);

        // Movie Details
        const details = document.createElement('div');
        details.classList.add('movie-details');
        details.innerHTML = `
                    <strong>Director:</strong> <a href="${movie._links.director.href}">${movie.director}</a><br>
                    <strong>Genre:</strong> <a href="${movie._links.genre.href}">${movie.genre}</a><br>
                    <strong>Country:</strong> <a href="${movie._links.country.href}">${movie.country}</a><br>
                    <strong>Release Date:</strong> ${movie.release_date}<br>
                    <strong>IMDB ID:</strong> <a href="https://www.imdb.com/title/${movie.imdb_id}" target="_blank">${movie.imdb_id}</a>
                `;
        movieDiv.appendChild(details);

        // Reviews
        movie.reviews.forEach(review => {
            const reviewDiv = document.createElement('div');
            reviewDiv.classList.add('review');
            reviewDiv.innerHTML = `
                        <strong>Review Date:</strong> ${review.review_date}<br>
                        <strong>Rating:</strong> ${review.rating}/5<br>
                        <strong>Comment:</strong> ${review.comment}<br>
                        <strong>Liked:</strong> ${review.is_liked ? 'Yes' : 'No'}
                    `;
            movieDiv.appendChild(reviewDiv);
        });

        container.appendChild(movieDiv);
    });
}
