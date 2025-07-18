window.addEventListener('DOMContentLoaded', () => {

    const logosContainer = document.getElementById("logos");

    // Elimina cualquier clon previo si existe
    const clonPrevio = logosContainer.querySelector('.slider-clone');
    if (clonPrevio) {
        clonPrevio.remove();
    }

    // Clonamos de nuevo el slider original
    const logos = document.getElementById("slider").cloneNode(true);
    logos.classList.add('slider-clone');
    logosContainer.appendChild(logos);

    //Acordion javascript
    const accordionButtons = document.querySelectorAll('.accordion__button');

    accordionButtons.forEach(button => {
        button.addEventListener('click', () => {
            const content = button.nextElementSibling;
            const icon = button.querySelector('.accordion__icon');

            // Cerrar todos los demás
            document.querySelectorAll('.accordion__content').forEach(c => {
                if (c !== content) c.classList.remove('open');
            });

            document.querySelectorAll('.accordion__icon').forEach(i => {
                if (i !== icon) {
                    i.classList.remove('fa-minus');
                    i.classList.add('fa-plus');
                }
            });

            // Alternar el actual
            if (content.classList.contains('open')) {
                content.classList.remove('open');
                icon.classList.remove('fa-minus');
                icon.classList.add('fa-plus');
            } else {
                content.classList.add('open');
                icon.classList.remove('fa-plus');
                icon.classList.add('fa-minus');
            }
        });
    });



});

