document.getElementById('create-movie').addEventListener('click', submitForm);

async function submitForm() {
    const formData = {
        title: document.getElementById('title').value,
        releaseDate: document.getElementById('release_date').value,
        runtime: document.getElementById('runtime').value + ' min',
        director: document.getElementById('director').value,
        genre: document.getElementById('genre').value,
        country: document.getElementById('country').value,
        imdbId: document.getElementById('imdb_id').value,
        posterUrl: document.getElementById('poster_url').value
    };

    const username = 'UserA';
    const password = 'password';

    fetch('http://localhost:8080/movies', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic ' + btoa(username + ':' + password)
        },
        body: JSON.stringify(formData)
    })
        .then((response) => {
            if(response.ok) {
                return response.json()
            }
            else {
                throw new Error('Failed to add movie.');
            }
        })
        .then((data) => {
            alert("Movie added successfully!");
            document.getElementById('movieForm').reset();
            window.location.href = data._links.self.href;
        })
        .catch(error => {
            console.error('Error:', error)
            alert(error);
        });
}