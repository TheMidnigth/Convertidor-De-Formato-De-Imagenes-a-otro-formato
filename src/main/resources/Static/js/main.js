window.addEventListener('DOMContentLoaded', () => {

    //Animaciones de entradas con GSAP.from
    gsap.from(".header__logo", {
        x: -300,
        duration: 1,
        opacity: 0,
        ease: "back.out"
    });

    gsap.from(".list__item", {
        y: -100,
        ease: "back.out",
        duration: 1,
        stagger: .10
    });

    gsap.from(".header__item", {
        x: 400,
        opacity: 0,
        duration: 1
    });

    //Cada linea,palabra y letra actua por si sola con la libreria splitType
    const text = new SplitType('.information__title', { types: "words,chars" });


    //Animacion de entrada de cada letra con gsap
    text.chars.forEach((char, index) => {

        //Creamos una linea de tiempo en donde decidimos que animacion se ejecuta primero
        let chartTL = gsap.timeline();

        //primero se ejectura esta animacion de entrada
        chartTL.from(char, {
            y: gsap.utils.random(-250, 250),
            x: gsap.utils.random(-300, 300),
            rotate: gsap.utils.random(-360, 360),
            opacity: 0,
            duration: .75,
            scale: gsap.utils.random(0, 2),
            ease: "back.out",
            delay: index * 0.01
        })

        //despues se ejectura esta animacion de entrada
        chartTL.from(char, {
            color: `rgb(${gsap.utils.random(0, 255)},${gsap.utils.random(0, 255)},${gsap.utils.random(0, 255)})`,
            duration: 1
        }, "-=.25")

        //aplicamos un efecto hover con el addEventListener "mouseenter"
        char.addEventListener("mouseenter", charsHover);

        function charsHover() {

            //Creamos una linea de tiempo en donde decidimos que animacion se ejecuta primero
            let chartTL = gsap.timeline();

            //con gsap.to le voy a decir a las letras que "hagan esto"
            chartTL.to(char, {
                y: gsap.utils.random(-50, 50),
                x: gsap.utils.random(-50, 50),
                rotate: gsap.utils.random(-90, 90),
                scale: gsap.utils.random(0.5, 1.5),
                color: `rgb(${gsap.utils.random(0, 255)},${gsap.utils.random(0, 255)},${gsap.utils.random(0, 255)})`,
                ease: "back.out",
                onStart:() => {
                    char.removeEventListener("mouseenter", charsHover);
                }
            })

            chartTL.to(char,{
                y: 0,
                x: 0,
                rotate: 0,
                scale: 1,
                color: '#000000',
                delay:1,
                ease: "back.out",
                onComplete:() => {
                    setTimeout(() => {
                        char.addEventListener("mouseenter", charsHover);
                    }, 100);
                    
                }
            })
            
        }

    });

    const text2 = new SplitType('.information__subtitulo', { types: "words,chars" });

    text2.chars.forEach((char,index) => {

        let chartTL = gsap.timeline();
        
        chartTL.from(char,{
            y:300,
            ease: "back.out",
            opacity:1,
            duration:.55,
            delay:index * 0.001
        })
    })

    gsap.from(".button__item",{
        y:400,
        delay:.25,
        duration:.65,
        opacity:0,
        ease:"back.out"
    })

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

