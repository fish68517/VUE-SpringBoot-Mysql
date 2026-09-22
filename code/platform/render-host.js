// App renderjs is mounted on a detached Vue placeholder, unlike H5's mixin.
// Use the change:prop element descriptor (or its owner), never that placeholder.
export function renderHost(renderer, owner, instance, selector) {
  const candidates = [instance?.$el, owner?.$el, renderer.$ownerInstance?.$el, renderer.host, renderer.$el]
  for (const element of candidates) {
    if (element?.nodeType !== 1) continue
    return selector ? element.querySelector(selector) || element : element
  }
  return null
}
