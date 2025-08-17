// gallery_tag_editor.js
// Требует: Bootstrap (modal), уже подключённый gallery.js (где есть mainPhoto, photoList, currentIndex), gallery_tags.js (loadTags)

(function () {
  // Только для админов — кнопки/скрипты можно подключать при помощи sec:authorize, но дополнительно проверим флаг
  const isAdmin = document.body.getAttribute('data-role') === 'ADMIN';
  if (!isAdmin) return;

  const photo = document.getElementById('mainPhoto');
  const layer = document.getElementById('tagsLayer');
  if (!photo || !layer) return;

  let selectionBox = null;
  let startX = 0, startY = 0;

  // Вспомогательная: получить относительные координаты внутри изображения
  function getOffsetInLayer(evt) {
    const rect = layer.getBoundingClientRect();
    const x = evt.clientX - rect.left;
    const y = evt.clientY - rect.top;
    // ограничим в пределах слоя
    return {
      x: Math.max(0, Math.min(rect.width, x)),
      y: Math.max(0, Math.min(rect.height, y)),
      w: rect.width,
      h: rect.height
    };
  }

  layer.addEventListener('mousedown', (e) => {
    // Начинаем рисовать рамку
    const pos = getOffsetInLayer(e);
    startX = pos.x;
    startY = pos.y;

    selectionBox = document.createElement('div');
    selectionBox.className = 'photo-tag-select';
    Object.assign(selectionBox.style, {
      position: 'absolute',
      left: `${startX}px`,
      top: `${startY}px`,
      width: '0px',
      height: '0px',
      border: '2px dashed #D9825B',
      background: 'rgba(217,130,91,0.12)',
      pointerEvents: 'none',
      zIndex: 5
    });
    layer.appendChild(selectionBox);

    function onMouseMove(ev) {
      const cur = getOffsetInLayer(ev);
      const w = cur.x - startX;
      const h = cur.y - startY;

      const left = w < 0 ? cur.x : startX;
      const top = h < 0 ? cur.y : startY;
      const width = Math.abs(w);
      const height = Math.abs(h);

      selectionBox.style.left = `${left}px`;
      selectionBox.style.top = `${top}px`;
      selectionBox.style.width = `${width}px`;
      selectionBox.style.height = `${height}px`;
    }

    function onMouseUp(ev) {
      layer.removeEventListener('mousemove', onMouseMove);
      layer.removeEventListener('mouseup', onMouseUp);

      // Если рамка слишком маленькая — отменяем
      const rect = selectionBox.getBoundingClientRect();
      if (parseInt(selectionBox.style.width, 10) < 8 || parseInt(selectionBox.style.height, 10) < 8) {
        selectionBox.remove();
        selectionBox = null;
        return;
      }

      // Открываем модалку
      const x = parseInt(selectionBox.style.left, 10);
      const y = parseInt(selectionBox.style.top, 10);
      const w = parseInt(selectionBox.style.width, 10);
      const h = parseInt(selectionBox.style.height, 10);

      // Пробрасываем в форму
      document.getElementById('tagX').value = x;
      document.getElementById('tagY').value = y;
      document.getElementById('tagWidth').value = w;
      document.getElementById('tagHeight').value = h;
      document.getElementById('tagFilename').value = window.galleryData?.photos?.[window.currentIndex] || '';

      const modalEl = document.getElementById('tagModal');
      const modal = new bootstrap.Modal(modalEl);
      modal.show();

      // Если модалку закрыли — убираем временную рамку
      modalEl.addEventListener('hidden.bs.modal', () => {
        if (selectionBox) {
          selectionBox.remove();
          selectionBox = null;
        }
      }, { once: true });
    }

    layer.addEventListener('mousemove', onMouseMove);
    layer.addEventListener('mouseup', onMouseUp);
  });

  // Сабмит формы modalki
  const form = document.getElementById('tagForm');
  if (form) {
    form.addEventListener('submit', (e) => {
      e.preventDefault();

      const relativeId = document.getElementById('relativeSelect').value;
      const filename = document.getElementById('tagFilename').value;
      const x = parseInt(document.getElementById('tagX').value, 10);
      const y = parseInt(document.getElementById('tagY').value, 10);
      const width = parseInt(document.getElementById('tagWidth').value, 10);
      const height = parseInt(document.getElementById('tagHeight').value, 10);

      if (!relativeId || !filename) return;

      // CSRF из скрытого инпута в модалке
      const csrfInput = document.querySelector('#tagForm #_csrf');
      const csrfToken = csrfInput ? csrfInput.value : null;

      fetch('/gallery/tags/save', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(csrfToken ? { 'X-CSRF-TOKEN': csrfToken } : {})
        },
        body: JSON.stringify({ relativeId, filename, x, y, width, height })
      })
        .then(res => {
          if (!res.ok) throw new Error('Save failed');
          return res.json();
        })
        .then(() => {
          bootstrap.Modal.getInstance(document.getElementById('tagModal'))?.hide();
          selectionBox?.remove();
          selectionBox = null;
          // Перерисуем теги
          if (filename) loadTags(filename);
        })
        .catch(err => {
          console.error(err);
          // тут можно показать тост/алерт
        });
    });
  }
})();
