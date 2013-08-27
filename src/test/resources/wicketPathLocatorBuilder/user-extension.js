LocatorBuilders.add('wicketpath', function(e) {
        this.log.debug("wicketpath: e=" + e);
        if (e.attributes && e.hasAttribute("wicketpath")) {
            this.log.info("found attribute " + e.getAttribute("wicketpath"));
            return "//" + this.xpathHtmlElement(e.nodeName.toLowerCase()) +
                "[@wicketpath=" + this.attributeValue(e.getAttribute("wicketpath")) + "]";
        }
        return null;
    });

LocatorBuilders.order.unshift(LocatorBuilders.order.pop());