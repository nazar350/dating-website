let selectedGender = null;
function selectGender(value) {
    console.log("selectGender called with value:", value);
    selectedGender = value;
    document.getElementById('gender').value = value;

    const maleButton = document.getElementById('male');
    const femaleButton = document.getElementById('female');
    if (value === true) {
        maleButton.classList.remove('unselected');
        femaleButton.classList.add('unselected');
    } else {
        maleButton.classList.add('unselected');
        femaleButton.classList.remove('unselected');
    }
}

let selectedInterests = [];
function toggleInterest(interest) {
    console.log("toggleInterest called with interest:", interest);
    const index = selectedInterests.indexOf(interest);
    const button = document.querySelector(`button[data-interest="${interest}"]`);

    if (index === -1) {
        if (selectedInterests.length < 5) {
            selectedInterests.push(interest);
            button.classList.remove('unselected');
        } else {
            alert('You can select up to 5 interests.');
        }
    } else {
        selectedInterests.splice(index, 1);
        button.classList.add('unselected');
    }
    document.getElementById('interests').value = selectedInterests.join(',');
}

function previewPhoto(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const img = document.getElementById('preview-img');
            const text = document.getElementById('photo-text');
            img.src = e.target.result;
            img.style.display = 'block';
            text.style.display = 'none';
        };
        reader.readAsDataURL(file);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    // Ініціалізація Gender
    const genderField = document.getElementById('gender');
    if (genderField) {
        const maleButton = document.getElementById('male');
        const femaleButton = document.getElementById('female');
        maleButton.classList.add('unselected');
        femaleButton.classList.add('unselected');

        maleButton.addEventListener('click', function() {
            selectGender(true);
        });
        femaleButton.addEventListener('click', function() {
            selectGender(false);
        });

        if (genderField.value === 'true') {
            selectGender(true);
        } else if (genderField.value === 'false') {
            selectGender(false);
        }
    }

    // Ініціалізація Interests
    const interestsField = document.getElementById('interests');
    if (interestsField) {
        const allInterestButtons = document.querySelectorAll('.interest-button[data-interest]');
        allInterestButtons.forEach(button => {
            button.classList.add('unselected');
            button.addEventListener('click', function() {
                const interest = button.getAttribute('data-interest');
                toggleInterest(interest);
            });
        });

        if (interestsField.value) {
            selectedInterests = interestsField.value.split(',').filter(i => i);
            selectedInterests.forEach(interest => {
                const button = document.querySelector(`button[data-interest="${interest}"]`);
                if (button) {
                    button.classList.remove('unselected');
                }
            });
        }
    }

    // Додаємо обробник для попереднього перегляду фото
    const photoInput = document.getElementById('photoPath');
    if (photoInput) {
        photoInput.addEventListener('change', previewPhoto);
    }
});