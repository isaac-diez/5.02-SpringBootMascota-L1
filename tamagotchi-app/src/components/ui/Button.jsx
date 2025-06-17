const Button = ({
  children,
  onClick,
  type = 'button',
  variant = 'primary',
  disabled = false,
  fullWidth = false
}) => {
  const getVariantClasses = () => {
    switch(variant) {
      case 'secondary': return 'bg-gray-500 hover:bg-gray-600'
      case 'danger': return 'bg-red-500 hover:bg-red-600'
      default: return 'bg-blue-500 hover:bg-blue-600'
    }
  }

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={`py-2 px-4 rounded text-white ${getVariantClasses()} ${
        disabled ? 'opacity-50 cursor-not-allowed' : ''
      } ${fullWidth ? 'w-full' : ''}`}
    >
      {children}
    </button>
  )
}

export default Button