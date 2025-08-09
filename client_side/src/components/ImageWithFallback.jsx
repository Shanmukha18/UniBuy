import { useState } from 'react';

const ImageWithFallback = ({ 
  src, 
  alt, 
  className = '', 
  fallbackIcon = null,
  fallbackClassName = 'w-full h-full flex items-center justify-center text-gray-400',
  onError = null,
  onLoad = null,
  ...props 
}) => {
  const [hasError, setHasError] = useState(false);

  const handleError = (e) => {
    setHasError(true);
    if (onError) {
      onError(e);
    }
  };

  const handleLoad = (e) => {
    setHasError(false);
    if (onLoad) {
      onLoad(e);
    }
  };

  // Default fallback icon
  const defaultFallbackIcon = (
    <svg className="w-16 h-16" fill="currentColor" viewBox="0 0 20 20">
      <path fillRule="evenodd" d="M4 3a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V5a2 2 0 00-2-2H4zm12 12H4l4-8 3 6 2-4 3 6z" clipRule="evenodd" />
    </svg>
  );

  return (
    <>
      {src && src.trim() !== '' && !hasError ? (
        <img
          src={src}
          alt={alt || 'Image'}
          className={className}
          onError={handleError}
          onLoad={handleLoad}
          {...props}
        />
      ) : null}
      <div className={`${fallbackClassName} ${src && src.trim() !== '' && !hasError ? 'hidden' : ''}`}>
        {fallbackIcon || defaultFallbackIcon}
      </div>
    </>
  );
};

export default ImageWithFallback;
