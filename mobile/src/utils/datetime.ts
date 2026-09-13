export function parseApiDateTime(value?: string | null): Date | null {
  if (!value) {
    return null
  }

  const raw = String(value).trim()
  if (!raw) {
    return null
  }

  if (/^\d+$/.test(raw)) {
    const parsed = new Date(Number(raw))
    return Number.isNaN(parsed.getTime()) ? null : parsed
  }

  const dateOnlyMatch = raw.match(/^(\d{4})-(\d{2})-(\d{2})$/)
  if (dateOnlyMatch) {
    const [, year, month, day] = dateOnlyMatch
    return new Date(Number(year), Number(month) - 1, Number(day), 0, 0, 0)
  }

  const localDateTimeMatch = raw.match(/^(\d{4})-(\d{2})-(\d{2})[ T](\d{2}):(\d{2})(?::(\d{2}))?$/)
  if (localDateTimeMatch) {
    const [, year, month, day, hour, minute, second] = localDateTimeMatch
    return new Date(
      Number(year),
      Number(month) - 1,
      Number(day),
      Number(hour),
      Number(minute),
      Number(second || '0')
    )
  }

  const normalized = raw.includes('T') ? raw : raw.replace(/-/g, '/')
  const parsed = new Date(normalized)
  return Number.isNaN(parsed.getTime()) ? null : parsed
}

export function hasExplicitTime(value?: string | null) {
  if (!value) {
    return false
  }
  return /(?:\d{2}:\d{2})|T\d{2}:\d{2}/.test(value)
}

export function formatApiDateTime(value?: string | null, fallback = '') {
  if (!value) {
    return fallback
  }

  const parsed = parseApiDateTime(value)
  if (!parsed) {
    return value
  }

  const month = `${parsed.getMonth() + 1}`.padStart(2, '0')
  const day = `${parsed.getDate()}`.padStart(2, '0')
  if (!hasExplicitTime(value)) {
    return `${month}-${day}`
  }

  const hours = `${parsed.getHours()}`.padStart(2, '0')
  const minutes = `${parsed.getMinutes()}`.padStart(2, '0')
  return `${month}-${day} ${hours}:${minutes}`
}

export function toApiTimestamp(value?: string | null) {
  const parsed = parseApiDateTime(value)
  if (!parsed) {
    return 0
  }
  return parsed.getTime()
}

export function toApiDateKey(value?: string | null) {
  const parsed = parseApiDateTime(value)
  if (!parsed) {
    return ''
  }
  const year = parsed.getFullYear()
  const month = `${parsed.getMonth() + 1}`.padStart(2, '0')
  const day = `${parsed.getDate()}`.padStart(2, '0')
  return `${year}-${month}-${day}`
}
