
(function() {

	var Event = tinymce.dom.Event;
	
	var baseStyle="background-color: #E5EFFD; border: 1px solid #ABC6DD; color:black; padding:5px;";
	var tileStyle=baseStyle;
	var contentStyle=baseStyle+"height:300px;";
	
	tinymce.create('tinymce.plugins.BRIX', {
		
		init : function(ed, url) {

			var t = this;
			t.editor=ed;
			
			
			ed.onNodeChange.addToTop(function(ed, cm, n) {
				var sc, ec;

				// Block if start or end is inside a non editable element
				sc = ed.dom.getParent(ed.selection.getStart(), function(n) {
					return ed.dom.hasClass(n, "brixtile")|ed.dom.hasClass(n, "brixcontent");
				});

				ec = ed.dom.getParent(ed.selection.getEnd(), function(n) {
					return ed.dom.hasClass(n, "brixtile")|ed.dom.hasClass(n, "brixcontent");
				});

				// Block or unblock
				if (sc || ec) {
					t._setDisabled(1);
					return false;
				} else
					t._setDisabled(0);
			});
			
			
			
			
			ed.onBeforeSetContent.add(function(ed, o) {
				o.content=t._tohtml(o.content);
			});

			ed.onPostProcess.add(function(ed, o) {
				if (o.set) {
					o.content=t._tohtml(o.content);
				} else if (o.get) {
					o.content=t._fromhtml(o.content);
				}
			});
		},

		getInfo : function() {
			return {
				longname : 'Brix CMS Plugin',
				author : 'ivaynberg',
				authorurl : '',
				infourl : '',
				version : tinymce.majorVersion + "." + tinymce.minorVersion
			};
		},


		_fromhtml:function(s) {
			
			function rep(re, str) {
				s = s.replace(re, str);
			};

			s = tinymce.trim(s);
			rep(/<div\s*?class="brixcontent".*?<\/div>/gi,"<brix:content/>");
			rep(/<div\s*?id="(.*?)".*?class="brixtile".*?<\/div>/gi,"<brix:tile id=\"$1\"/>");
			return s;
		},	
		

	_tohtml:function(content) {
		function rep(re, str) {
				content = content.replace(re, str);
			};
		
		rep(/<brix:content.*?\/>/gi,"<div class=\"brixcontent\" style=\""+contentStyle+"\">Brix Content Block</div>");
		rep(/<brix:tile.*?id=\"(.*?)\".*?\/>/gi,"<div class=\"brixtile\" id=\"$1\" style=\""+tileStyle+"\">Brix Tile: $1</div>");
		return content;
		
	},



		_block : function(ed, e) {
			return Event.cancel(e);
		},

		_setDisabled : function(s) {
			var t = this, ed = t.editor;

			tinymce.each(ed.controlManager.controls, function(c) {
				c.setDisabled(s);
			});

			if (s !== t.disabled) {
				if (s) {
					ed.onKeyDown.addToTop(t._block);
					ed.onKeyPress.addToTop(t._block);
					ed.onKeyUp.addToTop(t._block);
					ed.onPaste.addToTop(t._block);
				} else {
					ed.onKeyDown.remove(t._block);
					ed.onKeyPress.remove(t._block);
					ed.onKeyUp.remove(t._block);
					ed.onPaste.remove(t._block);
				}

				t.disabled = s;
			}
		}




});

	// Register plugin
	tinymce.PluginManager.add('brix', tinymce.plugins.BRIX);
})();