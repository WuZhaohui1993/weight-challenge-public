(function () {
  const toast = document.getElementById('toast');
  let toastTimer;

  window.fdShowToast = function fdShowToast(text) {
    if (!toast) return;
    toast.textContent = text;
    toast.classList.add('is-show');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => toast.classList.remove('is-show'), 1400);
  };

  document.querySelectorAll('[data-nav]').forEach((item) => {
    item.addEventListener('click', () => {
      window.location.href = item.dataset.nav;
    });
  });

  document.querySelectorAll('[data-toast]').forEach((item) => {
    item.addEventListener('click', () => window.fdShowToast(item.dataset.toast));
  });

  document.querySelectorAll('[data-sheet-open]').forEach((item) => {
    item.addEventListener('click', () => {
      document.querySelector(item.dataset.sheetOpen)?.classList.add('is-show');
    });
  });

  document.querySelectorAll('[data-sheet-close]').forEach((item) => {
    item.addEventListener('click', () => {
      item.closest('.sheet')?.classList.remove('is-show');
    });
  });

  document.querySelectorAll('[data-tab-target]').forEach((item) => {
    item.addEventListener('click', () => {
      const group = item.dataset.tabGroup;
      const target = item.dataset.tabTarget;
      document.querySelectorAll(`[data-tab-group="${group}"]`).forEach((tab) => tab.classList.remove('is-active'));
      item.classList.add('is-active');
      document.querySelectorAll(`[data-panel-group="${group}"]`).forEach((panel) => {
        panel.hidden = panel.id !== target;
      });
    });
  });

  const queryTab = new URLSearchParams(window.location.search).get('tab');
  if (queryTab) {
    document.querySelector(`[data-initial-tab="${queryTab}"]`)?.click();
    if (['dinner', 'move', 'water'].includes(queryTab)) {
      document.querySelector(`[data-intent="${queryTab}"]`)?.click();
    }
  }
})();
