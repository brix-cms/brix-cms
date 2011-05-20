(function() {

    var Drag = {

        /**
         * Initializes the dragging on the specified element.
         * Element's onmousedown will be replaced by generated handler.
         *
         * @param {Element} element - element clicking on which the drag should begin
         * @param {Function} onDragBegin - handler called at the begin on dragging - passed element as first parameter
         * @param {Function} onDragEnd - handler called at the end of dragging - passed element as first parameter
         * @param {Function} onDrag - handler called during dragging - passed element and mouse deltas
         */
        init: function(element, onDragBegin, onDragEnd, onDrag) {

            if (typeof(onDragBegin) == "undefined")
                onDragBegin = Wicket.emptyFunction;
            if (typeof(onDragEnd) == "undefined")
                onDragEnd = Wicket.emptyFunction;
            if (typeof(onDrag) == "undefined")
                onDrag = Wicket.emptyFunction;

            element.wicketOnDragBegin = onDragBegin;
            element.wicketOnDrag = onDrag;
            element.wicketOnDragEnd = onDragEnd;


            // set the mousedown handler
            Wicket.Event.add(element, "mousedown", Drag.mouseDownHandler);
        },

        mouseDownHandler: function(e) {
            e = Wicket.fixEvent(e);

            var src = e.target || e.srcElement;
            if (src.tagName != "DIV") {
                return;
            }

            var element = this;

            // HACK - for safari stopPropagation doesn't work well because
            // it also prevents scrollbars and form components getting the
            // event. Therefore for safari the 'ignore' flag is set on event.
            if (typeof(e.ignore) == "undefined") {

                Wicket.stopEvent(e);

                if (e.preventDefault) {
                    e.preventDefault();
                }

                element.wicketOnDragBegin(element);

                element.lastMouseX = e.clientX;
                element.lastMouseY = e.clientY;

                element.old_onmousemove = document.onmousemove;
                element.old_onmouseup = document.onmouseup;
                element.old_onselectstart = document.onselectstart;
                element.old_onmouseout = document.onmouseout;

                document.onselectstart = function() {
                    return false;
                }
                document.onmousemove = Drag.mouseMove;
                document.onmouseup = Drag.mouseUp;
                document.onmouseout = Drag.mouseOut;

                Drag.current = element;

                return false;
            }

        },

        /**
         * Deinitializes the dragging support on given element.
         */
        clean: function(element) {
            element.onmousedown = null;
        },

        /**
         * Called when mouse is moved. This method fires the onDrag event
         * with element instance, deltaX and deltaY (the distance
         * between this call and the previous one).

         * The onDrag handler can optionally return an array of two integers
         * - the delta correction. This is used, for example, if there is
         * element being resized and the size limit has been reached (but the
         * mouse can still move).
         *
         * @param {Event} e
         */
        mouseMove: function(e) {
            e = Wicket.fixEvent(e);
            var o = Drag.current;

            // this happens sometimes in Safari
            if (e.clientX < 0 || e.clientY < 0) {
                return;
            }

            if (o != null) {
                var deltaX = e.clientX - o.lastMouseX;
                var deltaY = e.clientY - o.lastMouseY;

                var res = o.wicketOnDrag(o, deltaX, deltaY, e);

                if (res == null)
                    res = [0, 0];

                o.lastMouseX = e.clientX + res[0];
                o.lastMouseY = e.clientY + res[1];
            }

            return false;
        },

        /**
         * Called when the mouse button is released.
         * Cleans all temporary variables and callback methods.
         *
         * @param {Event} e
         */
        mouseUp: function(e) {
            e = Wicket.fixEvent(e);
            var o = Drag.current;

            if (o != null && typeof(o) != "undefined") {
                o.wicketOnDragEnd(o);

                o.lastMouseX = null;
                o.lastMouseY = null;

                document.onmousemove = o.old_onmousemove;
                document.onmouseup = o.old_onmouseup;
                document.onselectstart = o.old_onselectstart;

                document.onmouseout = o.old_onmouseout;

                o.old_mousemove = null;
                o.old_mouseup = null;
                o.old_onselectstart = null;
                o.old_onmouseout = null;

                Drag.current = null;
            }
        },

        /**
         * Called when mouse leaves an element. We need this for firefox, as otherwise
         * the dragging would continue after mouse leaves the document.
         * Unfortunately this break dragging in firefox immediately after the mouse leaves
         * page.
         */
        mouseOut: function(e) {
            if (false && Wicket.Browser.isGecko()) {
                // other browsers handle this more gracefully
                e = Wicket.fixEvent(e);

                if (e.target.tagName == "HTML") {
                    Drag.mouseUp(e);
                }
            }
        }


    };


    /**
     * Convenience methods for getting and setting cookie values.
     */
    var Cookie = {

        /**
         * Returns the value for cookie of given name.
         * @param {String} name - name of cookie
         */
        get: function(name) {
            if (document.cookie.length > 0) {
                start = document.cookie.indexOf(name + "=");
                if (start != -1) {
                    start = start + name.length + 1;
                    end = document.cookie.indexOf(";", start);
                    if (end == -1) {
                        end = document.cookie.length;
                    }
                    return unescape(document.cookie.substring(start, end))
                }
            } else {
                return null
            }
        },

        /**
         * Sets the value for cookie of given name.
         * @param {Object} name - name of cookie
         * @param {Object} value - new value
         * @param {Object} expiredays - how long will the cookie be persisted
         */
        set: function(name, value, expiredays) {
            var exdate = new Date();
            exdate.setDate(exdate.getDate() + expiredays);
            document.cookie = name + "=" + escape(value) + ";path=/" + ((expiredays == null) ? "" : ";expires=" + exdate);
        }
    };


    var genIdCounter = 0;

    var getElementId = function(element) {
        if (typeof(element.getAttribute("id")) == "string" && element.getAttribute("id").length > 0) {
            return element.getAttribute("id");
        } else {
            var id = "brix-toolbar-id-" + (++genIdCounter);
            element.setAttribute("id", id);
            return id;
        }
    };

    var mouseDown = function() {
    };

    var mouseMove = function(element, dX, dY) {
        var x = parseInt(element.style.left, 10);
        var y = parseInt(element.style.top, 10);

        x += dX;
        y += dY;

        element.style.left = x + "px";
        element.style.top = y + "px";
    };

    var mouseUp = function(element) {
        var x = parseInt(element.style.left, 10);
        var y = parseInt(element.style.top, 10);
        savePosition(x, y);
    };

    var setCookie = function(value) {
        Cookie.set("brix-revision", value);
    }

    var getCookie = function() {
        return Cookie.get("brix-revision");
    }

    var savePosition = function(x, y) {
        Cookie.set("brix-toolbar-position", x + "," + y);
    }

    var getPosition = function() {
        try {
            var pos = Cookie.get("brix-toolbar-position");
            if ((typeof pos) == "string") {
                return pos.split(',');
            }
        } catch (e) {
            return null;
        }
    }

    var html = "<div class='brix-toolbar' style='left:10px; top: 10px'><div class='brix-toolbar1'>" +
            " Switch to version <select size='1'></select> " +
            "</div></div>"


    var BrixToolbar = function(revisions, defaultRevision) {

        var d = document.createElement("div");
        document.body.appendChild(d);
        d.innerHTML = html;
        this.revisions = revisions;
        this.defaultRevision = defaultRevision;

        d = d.getElementsByTagName("div")[0];

        var pos = getPosition();
        if (pos != null) {
            d.style.left = pos[0] + "px";
            d.style.top = pos[1] + "px";
        }

        var select = d.getElementsByTagName("select")[0];
        this.selectId = getElementId(select);

        var revision = getCookie(revision) || defaultRevision;

        var selected = 0;

        for (var i = 0; i < revisions.length; ++i) {
            var rev = revisions[i];
            if (rev.value == revision) {
                selected = i;
            }
            var o = document.createElement("option");
            o.innerHTML = rev.name;
            select.appendChild(o);
        }

        select.selectedIndex = selected;

        Wicket.Event.add(select, "change", this.onChange.bind(this));

        Drag.init(d, mouseDown, mouseUp, mouseMove);
    };

    BrixToolbar.prototype.onChange = function() {
        var select = Wicket.$(this.selectId);
        var revision = this.revisions[select.selectedIndex].value;
        setCookie(revision);
        window.location.reload();
    };

    BrixToolbarInit = function(revisions, defaultRevision) {
        Wicket.Event.add(window, "load", function() {
            new BrixToolbar(revisions, defaultRevision);
        });
    };

})();