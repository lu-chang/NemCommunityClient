"use strict";

define(['jquery', 'ractive', 'Utils'], function($, Ractive, Utils) {
	return Ractive.extend({
        template: '#modal-template',
        isolated: true,
        listeners: [],
        open: function() {
            this.set('isActive', true);
            $('.modal--active.modal-container').focus().find('.modal').css({
                left: 'auto',
                top: 'auto'
            });
            if (this.get('extraModalClass')) {
                var modalClass = '.modal.' + this.component.name;
                var modal = $('.modal--active.modal-container').focus().find(modalClass);
                modal.addClass(this.get('extraModalClass'));
            }
            this.fire('modalOpened');
        },
        closeModal: function() {
            if (this.get('extraModalClass')) {
                var modalClass = '.modal.' + this.component.name;
                var modal = $('.modal--active.modal-container').focus().find(modalClass);
                modal.removeClass(this.get('extraModalClass'));
            }
            this.set('isActive', false);
            this.fire('modalClosed');
        },
        toggleOn: function(id) {
            Utils.toggleOn(this, id);
        },
        toggleOff: function(id) {
            Utils.toggleOff(this, id);
        },
        lockAction: function() {
            this.set('processing', true);
        },
        unlockAction: function() {
            this.set('processing', false);
        },
        onrender: function() {
            this.on({
                modalContainerClick: function(e) {
                    if (e.node === e.original.target) { //clicked outside modal
                        this.closeModal();
                    }
                },
                modalContainerKeyup: function(e) {
                    if (e.original.keyCode === 27 || (this.get('closeOnEnter') && e.original.keyCode === 13)) {
                        this.closeModal();
                    }
                }
            });
        },
        onunrender: function() {
            $.each(this.listeners, function() {
                this.cancel();
            });
            this.listeners = [];
        }
    });
});