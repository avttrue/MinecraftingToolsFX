var elements = document.querySelectorAll("[data-repainted='false']");
var count = 0;
if(elements != null)
{
    for (var i = 0; i < elements.length; i++)
    {
	    var element = elements[i];
	    var disp = element.style.display;
	    element.style.display = 'none';
	    var trick = element.offsetHeight;
	    element.style.display = disp;
	    element.dataset.repainted = 'true';
	    count++;
    }
}
count;