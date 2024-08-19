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
                    <strong>IMDb ID:</strong> <a href="https://www.imdb.com/title/${movie.imdb_id}" target="_blank">${movie.imdb_id}</a>
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