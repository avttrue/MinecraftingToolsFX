var elements = document.querySelectorAll('.new-line');
if (elements != null && elements.length > 0)
{
	var element = elements[elements.length - 1];
	var disp = element.style.display;
	element.style.display = 'none';
	var trick = element.offsetHeight;
	element.style.display = disp;
	element.dataset.repainted = 'true';
}
