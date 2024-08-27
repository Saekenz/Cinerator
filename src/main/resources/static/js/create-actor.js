document.getElementById('create-actor').addEventListener('click', submitActorForm);

function calculateAge(birthday) {
    const today = new Date()
    const birth = new Date(birthday);

    let age = today.getFullYear() - birth.getFullYear();
    const monthDiff = today.getMonth() - birth.getMonth();

    if(monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
        age--;
    }

    return age;
}

async function submitActorForm() {
    const birth = document.getElementById('birth_date').value
    const formData = {
        name: document.getElementById('name').value,
        birth_date: birth,
        birth_country: document.getElementById('birth_country').value,
        age: calculateAge(birth)
    };

    fetch('http://localhost:8080/actors', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    })
        .then((response) => {
            if(response.ok) {
                return response.json()
            }
            else {
                throw new Error('Failed to add actor.');
            }
        })
        .then((data) => {
            alert('Actor added successfully!');
            document.getElementById('actorForm').reset();
            window.location.href = data._links.self.href;
        })
        .catch(error => {
            console.error('Error:', error)
            alert(error);
        });
}