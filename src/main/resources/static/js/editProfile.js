document.addEventListener('DOMContentLoaded', () => {
    // Отримуємо елементи для інтересів
    const interestButtons = document.querySelectorAll('.interest-button');
    const selectedInterestsInput = document.getElementById('selected-interests');
    const interestsInput = document.getElementById('interests');
    const maxInterests = 5;
    let selectedInterests = [];

    // Обробка вибору інтересів
    interestButtons.forEach(button => {
        button.addEventListener('click', () => {
            const interest = button.getAttribute('data-interest');

            if (selectedInterests.includes(interest)) {
                // Якщо інтерес уже вибраний, знімаємо вибір
                selectedInterests = selectedInterests.filter(i => i !== interest);
                button.classList.remove('selected');
            } else if (selectedInterests.length < maxInterests) {
                // Додаємо інтерес, якщо не перевищено ліміт
                selectedInterests.push(interest);
                button.classList.add('selected');
            } else {
                alert(`You can select up to ${maxInterests} interests.`);
                return;
            }

            // Оновлюємо відображення вибраних інтересів
            selectedInterestsInput.value = selectedInterests.join(', ');
            interestsInput.value = selectedInterests.join(',');
        });
    });
});