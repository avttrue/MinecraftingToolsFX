var elements = document.querySelectorAll('.new-line');
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
    	count++;
    }
}
count;
