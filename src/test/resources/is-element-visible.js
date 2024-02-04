(function (element) {
    const rect = element.getBoundingClientRect();
    const vWidth = window.innerWidth || doc.documentElement.clientWidth;
    const vHeight = window.innerHeight || doc.documentElement.clientHeight;
    // Check if the element is out of bounds
    return !(rect.right < 0 || rect.bottom < 0 || rect.left > vWidth || rect.top > vHeight);
})(arguments[0]);