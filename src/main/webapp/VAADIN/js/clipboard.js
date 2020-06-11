window.com_vaadin_jsclipboard_JSClipboard = function() {

    var clipboardBtn = this;
    var state = this.getState();

    var ClipboardLibrary = function() {
        this.destroy();
        this.init();
        if (state.enableClipboard) {
            this.setup();
        }
    };

    ClipboardLibrary.prototype = function() {

        var init = function() {
            if (state.targetSelector) {
                trigger().dataset.clipboardTarget = state.targetSelector;
            }

            if (state.text) {
                trigger().dataset.clipboardText = state.text;
            }
        };

        var setup = function() {
            this.clipboard = new Clipboard("." + state.buttonClass);

            this.clipboard.on('success', function(e) {
                clipboardBtn.notifyStatus(true);
                e.clearSelection();
            });

            this.clipboard.on('error', function(e) {
                clipboardBtn.notifyStatus(false);
            });
        };

        var destroy = function() {
            if (this.clipboard) {
                this.clipboard.destroy();
            }
        };


        var trigger = function() {
            return document.getElementsByClassName(state.buttonClass)[0];
        };

        return {
            init: init,
            setup: setup,
            destroy: destroy
        };
    }();

    var clipboardLibrary = new ClipboardLibrary();
    this.onStateChange = function() {
        clipboardLibrary.destroy();

        clipboardLibrary = new ClipboardLibrary();
    };


};

