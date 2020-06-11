selectioncanvas_SelectioncanvasComponent =
        function () {
            // Create the component
            var mycomponent =
                    new mylibrary.SelectioncanvasComponent(this.getElement());

            // Handle changes from the server-side
            this.onStateChange = function () {
                mycomponent.setValue(this.getState().value);
            };

            // Pass user interaction to the server-side
            var self = this;
            mycomponent.click = function () {
                self.onClick(mycomponent.getValue());
            };
            var connector = this;
            mycomponent.click = function () {
                connector.onClick(mycomponent.getValue());
            };
        };



