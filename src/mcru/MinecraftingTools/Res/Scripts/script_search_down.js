var elements = document.querySelectorAll("SPAN, A, KBD"), searchText = "%1$s", startIndex = %2$d, nextIndex = 0;
   if(startIndex >= elements.length || startIndex < 0) startIndex = 0;
   for (var i = startIndex; i < elements.length; i++) {
      if (elements[i].innerText.toLowerCase().indexOf(searchText.toLowerCase()) > -1) {
        elements[i].scrollIntoView();
        sel = window.getSelection();
        sel.removeAllRanges();
        range = document.createRange();
        range.selectNodeContents(elements[i].firstChild);
        sel.addRange(range);
        nextIndex = i + 1;
        break;
      }
      nextIndex = 0;
   }
nextIndex;