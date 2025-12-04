export const maskIdNumber = (idNumber: string): string => {
  if (!idNumber || idNumber.length < 4) return idNumber
  return '*'.repeat(idNumber.length - 4) + idNumber.slice(-4)
}

export const maskPhone = (phone: string): string => {
  if (!phone || phone.length < 4) return phone
  return phone.slice(0, 3) + '****' + phone.slice(-4)
}

export const maskEmail = (email: string): string => {
  if (!email || !email.includes('@')) return email
  const [name, domain] = email.split('@')
  if (name.length <= 2) return '*' + name.slice(-1) + '@' + domain
  return name.slice(0, 2) + '*'.repeat(name.length - 2) + '@' + domain
}
