function displayMovieData(movieData) {
    const container = document.getElementById('movie-details');
    container.innerHTML = ''

    const movies = Array.isArray(movieData._embedded?.movieList) ? movieData._embedded.movieList : [movieData];

    movies.forEach(movie => {
        const movieDiv = document.createElement('div');
        movieDiv.classList.add('movie');

        // Movie Details
        const details = document.createElement('div');
        details.classList.add('movie-details');
        details.innerHTML = `
            <div class="movie-content">
                <div class="movie-title">${movie.title}</div>
                <div class="movie-info">
                        <strong>Director:</strong> <a href="${movie._links.director.href}">${movie.director}</a><br>
                        <strong>Genre:</strong> <a href="${movie._links.genre.href}">${movie.genre}</a><br>
                        <strong>Country:</strong> <a href="${movie._links.country.href}">${movie.country}</a><br>
                        <strong>Release Date:</strong> ${movie.release_date}<br>
                        <strong>Runtime:</strong> ${movie.runtime}<br>
                        <strong>IMDb ID:</strong> <a href="https://www.imdb.com/title/${movie.imdb_id}" target="_blank">${movie.imdb_id}</a><br>
                </div>
                <div class="movie-reviews"/>
            </div>
        `;
        movieDiv.appendChild(details);

        // Reviews
        const movieReviews = details.querySelector('.movie-reviews');
        movie.reviews.forEach(review => {
            const reviewDiv = document.createElement('div');
            reviewDiv.classList.add('review');

            const ratingContainer = document.createElement('span');
            for (let i = 0; i < 5; i++) {
                const star = document.createElement('span');
                star.classList.add('star');
                star.innerHTML = i < review.rating ? '&#9733;' : '&#9734;';
                ratingContainer.appendChild(star);
            }

            const heartIcon = document.createElement('span');
            heartIcon.classList.add('heart');
            heartIcon.innerHTML = review.is_liked ? '&#10084;' : '&#9825;';
            ratingContainer.appendChild(heartIcon);

            reviewDiv.innerHTML += `<strong>Review by:</strong> ${review.username}<br>`;
            reviewDiv.appendChild(ratingContainer);
            reviewDiv.innerHTML += `
                        <br>${review.review_date}<br>
                        ${review.comment}<br>
                    `;
            movieReviews.appendChild(reviewDiv);
        });

        const posterDiv = document.createElement('div');
        const description = movie.title + ' poster';
        posterDiv.classList.add('movie-poster');
        posterDiv.innerHTML = `
            <img class="poster" src="${movie.poster_url}" alt="${description}" style="width:200px;height:297px;">
        `;
        movieDiv.append(posterDiv);

        container.appendChild(movieDiv);
    });
}