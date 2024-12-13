# Use Node.js 18-alpine base image
FROM node

# Set working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json (if present) to /app directory
COPY package.json .

# Install npm dependencies
RUN npm install

# Copy all files from current directory to /app in the container
COPY . .

# Expose port 3000 to the outside world
EXPOSE 3000

# Command to run the application
CMD ["npm", "start"]
